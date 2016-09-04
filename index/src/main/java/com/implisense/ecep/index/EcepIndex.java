package com.implisense.ecep.index;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.implisense.ecep.index.model.Company;
import com.implisense.ecep.index.model.SearchResult;
import com.implisense.ecep.index.model.SearchResultItem;
import com.implisense.ecep.index.util.ElasticsearchRequestExecutor;
import com.implisense.ecep.index.util.ObjectMapperFactory;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.source.FetchSourceContext;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Collections.emptyList;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.elasticsearch.index.query.QueryBuilders.*;
import static org.elasticsearch.search.aggregations.AggregationBuilders.terms;

public class EcepIndex {

    private static Logger LOGGER = LoggerFactory.getLogger(EcepIndex.class);

    private static final String INDEX_NAME = "ecep";

    private static final String COMPANY_TYPE = "company";

    private Client client;
    private String indexName;
    private boolean testMode = false;
    private Map<String, Long> globalCounts;

    /**
     * Creates the ecep index DAO.
     *
     * @param client The elasticsearch client connection to use.
     */
    public EcepIndex(Client client) {
        this(client, INDEX_NAME);
    }

    /**
     * Creates the ecep index DAO.
     *
     * @param client    The elasticsearch client connection to use.
     * @param indexName The index to operate on.
     */
    public EcepIndex(Client client, String indexName) {
        this.client = client;
        this.indexName = indexName;
        this.loadGlobalCounts();
    }

    public Client getClient() {
        return this.client;
    }

    public void closeClient() {
        this.client.close();
    }

    /**
     * Sets number of shards to 1 and number of replicas to 0. This has to be called before the
     * index is created.
     */
    public void setTestMode() {
        this.testMode = true;
    }

    public void createIndex() {
        this.createIndex(COMPANY_TYPE);
    }

    public void createIndex(String... types) {
        if (!this.client.admin().indices().exists(new IndicesExistsRequest(this.indexName)).actionGet().isExists()) {
            // extract the alphanumeric prefix from the index name as the prefix for the new real name and the settings
            // filename to search for in the classpath.
            String indexNamePrefix = this.indexName.replaceAll("([\\p{L}\\d]+).*", "$1");
            String realIndexName = this.indexName.equals(indexNamePrefix) ? indexNamePrefix
                    + DateTimeFormat.forPattern("-yyyy-MM-dd").withZone(DateTimeZone.forID("Europe/Berlin"))
                    .print(System.currentTimeMillis()) : this.indexName;
            if (this.client.admin().indices().exists(new IndicesExistsRequest(realIndexName)).actionGet().isExists()) {
                this.client.admin().indices().prepareAliases().addAlias(realIndexName, this.indexName).execute()
                        .actionGet();
            } else {
                try {
                    CreateIndexRequest createIndexRequest = new CreateIndexRequest(realIndexName);
                    for (String type : types) {
                        URL url = EcepIndex.class.getResource("mapping/" + type + ".json");
                        String typeMapping = Resources.toString(url, Charsets.UTF_8);
                        createIndexRequest.mapping(type, typeMapping);
                    }
                    Settings.Builder settings = Settings.settingsBuilder();
                    try {
                        URL url = EcepIndex.class.getResource("settings/" + indexNamePrefix + ".json");
                        settings = settings.loadFromSource(Resources
                                .toString(url, Charsets.UTF_8));
                    } catch (IllegalArgumentException e) {
                        // in this case there was no specific settings file found in the classpath for the current index
                        // name prefix, so we simply don't put any special settings to this index.
                    }
                    if (this.testMode) {
                        settings.put("number_of_shards", 1);
                        settings.put("number_of_replicas", 0);
                    }
                    createIndexRequest.settings(settings);

                    this.client.admin().indices().create(createIndexRequest).actionGet();
                    if (!realIndexName.equals(this.indexName)) {
                        this.client.admin().indices().prepareAliases().addAlias(realIndexName, this.indexName).get();
                    }
                    this.client.admin().cluster().health(new ClusterHealthRequest(this.indexName).waitForYellowStatus())
                            .actionGet();
                } catch (IOException e) {
                    throw new EcepIndexException("Error reading resource files at index creation!", e);
                }
            }
        }
    }

    public void deleteIndex() {
        if (this.client.admin().indices().exists(new IndicesExistsRequest(this.indexName)).actionGet().isExists()) {
            this.client.admin().indices().delete(new DeleteIndexRequest(this.indexName)).actionGet();
        }
    }

    /**
     * Removes all documents in this index. Please note that this method is only meant for testing
     * purposes and is not allowed in production. Thus, it does only work, if the
     * <code>testMode</code> flag is set to true!
     */
    public void clear() {
        if (this.testMode) {
            this.removeDocuments(matchAllQuery(), null);
        }
    }

    /**
     * Removes all matching documents from the current index.
     * If the type is provided, the query only matches documents of this type.
     *
     * @param query The documents to be deleted
     * @param type  Only documents of this type will be deleted (optional)
     */
    private void removeDocuments(QueryBuilder query, String type) {
        SearchRequestBuilder searchRequestBuilder = this.client.prepareSearch(indexName);
        if (type != null) {
            searchRequestBuilder.setTypes(type);
        }
        TimeValue keepAlive = new TimeValue(1, TimeUnit.MINUTES);
        SearchResponse response = ElasticsearchRequestExecutor.execute(searchRequestBuilder
                .setScroll(keepAlive).setQuery(query).addFields("_routing", "_parent").setSize(100));
        while (response.getHits().getHits().length > 0) {
            BulkRequestBuilder bulkRequestBuilder = this.client.prepareBulk();
            for (SearchHit hit : response.getHits()) {
                DeleteRequestBuilder deleteRequest =
                        this.client.prepareDelete(this.indexName, hit.getType(), hit.id());
                SearchHitField routing = hit.field("_routing");
                if (routing != null) {
                    deleteRequest.setRouting(routing.value());
                }
                SearchHitField parent = hit.field("_parent");
                if (parent != null) {
                    deleteRequest.setParent(parent.value());
                }
                bulkRequestBuilder.add(deleteRequest);
            }
            ElasticsearchRequestExecutor.execute(bulkRequestBuilder);
            response = ElasticsearchRequestExecutor.execute(
                    this.client.prepareSearchScroll(response.getScrollId()).setScroll(keepAlive));
        }
        this.client.prepareClearScroll().addScrollId(response.getScrollId()).get();
    }

    void commit() {
        this.client.admin().indices().refresh(new RefreshRequest(this.indexName)).actionGet();
        this.loadGlobalCounts();
    }

    // only used in test
    void putCompany(Company company) {
        if (this.testMode) {
            this.put(COMPANY_TYPE, company, company.getId());
        } else {
            throw new UnsupportedOperationException("This method is not meant to be called in production!");
        }
    }

    public void putCompanies(Collection<Company> companies) {
        this.putAll(COMPANY_TYPE, companies.stream().collect(toMap(Company::getId, identity())));
        this.commit();
    }

    /**
     * Performs a GET request against ES. Returns <code>null</code>, if the requested company was
     * not found.
     *
     * @param id The company ID
     * @return The company object for the given ID or <code>null</code>, if not found
     */
    public Company getCompany(String id) {
        Company company = null;
        GetResponse getResponse = this.client.prepareGet(this.indexName, COMPANY_TYPE, id).get();
        if (getResponse.isExists()) {
            company = this.parseCompany(getResponse);
        }
        return company;
    }

    /**
     * Performs a multi-GET request against ES. Returns the list of matching <code>Company</code>
     * objects in the same order as the ids in the provided list. In case of a non-matching ID, a
     * <code>null</code> value is added at the corresponding position in the list. This way the
     * returned list will always have the same size as the ids list.
     *
     * @param ids The company IDs
     * @return A <code>List</code> of <code>Company</code> objects, which may be empty but never
     * <code>null</code>.
     */
    public List<Company> getCompanies(List<String> ids) {
        if (ids.isEmpty()) {
            return emptyList();
        }
        List<Company> companies = new ArrayList<>(ids.size());
        MultiGetResponse multiGetResponse = this.client.prepareMultiGet().add(this.indexName, COMPANY_TYPE, ids).get();
        for (MultiGetItemResponse itemResponse : multiGetResponse.getResponses()) {
            GetResponse response = itemResponse.getResponse();
            if (response != null && response.isExists()) {
                companies.add(this.parseCompany(response));
            } else {
                companies.add(null); // to keep the order
            }
        }
        return companies;
    }

    private Company parseCompany(GetResponse response) {
        return this.parseCompany(response.getSourceAsString());
    }

    private Company parseCompany(String source) {
        try {
            return ObjectMapperFactory.instance().readValue(source, Company.class);
        } catch (IOException e) {
            throw new EcepIndexException("Exception parsing company document from index!", e);
        }
    }

    private void loadGlobalCounts() {
        Map<String, Long> globalCounts = new HashMap<>();
        if (this.client.admin().indices().exists(new IndicesExistsRequest(this.indexName)).actionGet().isExists()) {
            SearchRequestBuilder esRequest = this.client.prepareSearch(this.indexName).setTypes(COMPANY_TYPE)
                    .setQuery(matchAllQuery())
                    .addAggregation(terms("postCode").size(1000000).field("address.postCode").order(Terms.Order.term(true))
                            .subAggregation(terms("sicCode").size(10000).field("sicCodes").order(Terms.Order.term(true))))
                    .setSize(0);
            SearchResponse esResponse = esRequest.get();
            StringTerms postCodeAgg = esResponse.getAggregations().get("postCode");
            for (Terms.Bucket postCodeBucket : postCodeAgg.getBuckets()) {
                String bucketPostCode = postCodeBucket.getKeyAsString();
                StringTerms sicCodeAgg = postCodeBucket.getAggregations().get("sicCode");
                for (Terms.Bucket sicCodeBucket : sicCodeAgg.getBuckets()) {
                    String bucketSicCode = sicCodeBucket.getKeyAsString();
                    String globalKey = bucketPostCode + "\t" + bucketSicCode;
                    globalCounts.put(globalKey, sicCodeBucket.getDocCount());
                }
            }
        }
        this.globalCounts = globalCounts;
    }

    public SearchResult search(String query, String postCode, String sicCode, String category) {
        BoolQueryBuilder boolQuery = boolQuery();
        if (!isNullOrEmpty(query)) {
            boolQuery.must(matchQuery("name.analyzed", query));
        }
        if (!isNullOrEmpty(postCode)) {
            boolQuery.must(termQuery("address.postCode", postCode));
        }
        if (!isNullOrEmpty(sicCode)) {
            boolQuery.must(termQuery("sicCodes", sicCode));
        }
        if (!isNullOrEmpty(category)) {
            boolQuery.must(termQuery("category", category));
        }
        SearchRequestBuilder esRequest = this.client.prepareSearch(this.indexName).setTypes(COMPANY_TYPE)
                .setQuery(boolQuery)
                .addAggregation(terms("postCode").size(500000).field("address.postCode").order(Terms.Order.term(true))
                        .subAggregation(terms("sicCode").size(10000).field("sicCodes").order(Terms.Order.term(true))))
                .setSize(0);
        SearchResponse esResponse = esRequest.get();
        List<SearchResultItem> items = new ArrayList<>();
        StringTerms postCodeAgg = esResponse.getAggregations().get("postCode");
        for (Terms.Bucket postCodeBucket : postCodeAgg.getBuckets()) {
            String bucketPostCode = postCodeBucket.getKeyAsString();
            StringTerms sicCodeAgg = postCodeBucket.getAggregations().get("sicCode");
            for (Terms.Bucket sicCodeBucket : sicCodeAgg.getBuckets()) {
                String bucketSicCode = sicCodeBucket.getKeyAsString();
                String globalKey = bucketPostCode + "\t" + bucketSicCode;
                Long globalCount = this.globalCounts.get(globalKey);
                if (globalCount == null) {
                    globalCount = -1L;
                    LOGGER.warn("global count unknown for key: \"" + globalKey + "\"");
                }
                items.add(new SearchResultItem(bucketPostCode, bucketSicCode,
                        sicCodeBucket.getDocCount(), globalCount));
            }
        }
        return new SearchResult(esResponse.getHits().getTotalHits(), items);
    }

    private void put(String type, Object document, String id) {
        try {
            String jsonDoc = ObjectMapperFactory.instance().writeValueAsString(document);
            ElasticsearchRequestExecutor.execute(this.client.prepareIndex(this.indexName, type, id).setSource(jsonDoc));
        } catch (IOException e) {
            LOGGER.error("Error indexing document \"" + id + "\" of type \"" + type + "\"!", e);
        }
    }

    /**
     * Performs a bulk request including all index requests of the documents. The map contains the
     * documents as values and the corresponding IDs as keys. The map may be empty but must not be
     * null.
     */
    private <T extends Object> void putAll(String type, Map<String, T> documents) {
        if (documents.isEmpty()) {
            return; // do nothing on empty input to avoid the following ES exception.
        }
        try {
            BulkRequestBuilder bulkRequest = this.client.prepareBulk();
            for (Map.Entry<String, T> docEntry : documents.entrySet()) {
                String jsonDoc = (docEntry.getValue() instanceof String) ? (String) docEntry.getValue()
                        : ObjectMapperFactory.instance().writeValueAsString(docEntry.getValue());
                bulkRequest.add(this.client.prepareIndex(this.indexName, type, docEntry.getKey()).setSource(jsonDoc));
            }
            BulkResponse bulkResponse = ElasticsearchRequestExecutor.execute(bulkRequest);
            if (bulkResponse.hasFailures()) {
                LOGGER.error("Error bulk indexing documents  " + documents.keySet()
                        + " of type \"" + type + "\": " + bulkResponse.buildFailureMessage());
            }
        } catch (IOException e) {
            LOGGER.error("Error bulk indexing documents  " + documents.keySet() + " of type \""
                    + type + "\"!", e);
        }
    }
}
