package com.implisense.ecep.index;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.implisense.ecep.index.model.Company;
import com.implisense.ecep.index.util.ElasticsearchRequestExecutor;
import com.implisense.ecep.index.util.ObjectMapperFactory;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.search.SearchHit;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class EcepIndex {

    private static Logger LOGGER = LoggerFactory.getLogger(EcepIndex.class);

    private static final String COMPANY_TYPE = "company";

    private Client client;
    private String indexName;
    protected boolean testMode = false;

    /**
     * Creates the ecep index DAO.
     *
     * @param client    The elasticsearch client connection to use.
     * @param indexName The index to operate on.
     */
    public EcepIndex(Client client, String indexName) {
        this.client = client;
        this.indexName = indexName;
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

    public void createIndex(String... types) throws IOException {
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
                CreateIndexRequest createIndexRequest = new CreateIndexRequest(realIndexName);
                for (String type : types) {
                    URL url = Resources.getResource("type-mappings/" + type + ".json");
                    String typeMapping = Resources.toString(url, Charsets.UTF_8);
                    createIndexRequest.mapping(type, typeMapping);
                }
                Settings.Builder settings = Settings.settingsBuilder();
                try {
                    URL url = Resources.getResource("index-settings/" + indexNamePrefix + ".json");
                    settings = settings.loadFromSource(Resources
                            .toString(url, Charsets.UTF_8));
                } catch (IllegalArgumentException e) {
                    // in this case there was no specific settings file found in the classpath for the current index
                    // name
                    // prefix, so we simply don't add any special settings to this index.
                }
                if (this.testMode) {
                    settings.put("number_of_shards", 1);
                    settings.put("number_of_replicas", 0);
                }
                createIndexRequest.settings(settings);

                this.client.admin().indices().create(createIndexRequest).actionGet();
                if (!realIndexName.equals(this.indexName)) {
                    this.client.admin().indices().prepareAliases().addAlias(realIndexName, this.indexName).execute()
                            .actionGet();
                }
                this.client.admin().cluster().health(new ClusterHealthRequest(this.indexName).waitForYellowStatus())
                        .actionGet();
            }
        }
    }

    public void commit() {
        this.client.admin().indices().refresh(new RefreshRequest(this.indexName)).actionGet();
    }

    public void addCompany(Company company) {
        this.add(COMPANY_TYPE, company, company.getId());
    }

    public void addCompanies(Collection<Company> companies) {
        this.addAll(COMPANY_TYPE, companies.stream().collect(toMap(Company::getId, identity())));
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

    private void add(String type, Object document, String id) {
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
    private <T extends Object> void addAll(String type, Map<String, T> documents) {
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

    private Company parseCompany(GetResponse response) {
        return this.parseCompany(response.getSourceAsString());
    }

    private Company parseCompany(SearchHit hit) {
        return this.parseCompany(hit.getSourceAsString());
    }

    private Company parseCompany(String source) {
        try {
            return ObjectMapperFactory.instance().readValue(source, Company.class);
        } catch (IOException e) {
            throw new EcepIndexException("Exception parsing company document from index!", e);
        }
    }

}
