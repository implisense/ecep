package com.implisense.ecep.api.config;

import io.dropwizard.Configuration;

public class EcepConfig extends Configuration {

    private ElasticsearchConfig elasticsearch = new ElasticsearchConfig();

    public ElasticsearchConfig getElasticsearch() {
        return elasticsearch;
    }

    public void setElasticsearch(ElasticsearchConfig elasticsearch) {
        this.elasticsearch = elasticsearch;
    }
}
