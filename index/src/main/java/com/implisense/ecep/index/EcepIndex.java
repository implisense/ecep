package com.implisense.ecep.index;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class EcepIndex {

    private static Logger LOGGER = LoggerFactory.getLogger(EcepIndex.class);

    private static final String CLUSTER_NAME = "ecep";
    private static final String INDEX_NAME = "ecep";
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

}
