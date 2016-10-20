package com.implisense.ecep.api.config;

import io.dropwizard.Configuration;

public class EcepConfig extends Configuration {

    private ElasticsearchConfig elasticsearch = new ElasticsearchConfig();

    private GeocoderConfig geocoder = new GeocoderConfig();

    public ElasticsearchConfig getElasticsearch() {
        return elasticsearch;
    }

    public void setElasticsearch(ElasticsearchConfig elasticsearch) {
        this.elasticsearch = elasticsearch;
    }

    public GeocoderConfig getGeocoder() {
        return geocoder;
    }

    public void setGeocoder(GeocoderConfig geocoder) {
        this.geocoder = geocoder;
    }
}
