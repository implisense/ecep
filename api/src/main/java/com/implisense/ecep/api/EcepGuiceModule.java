package com.implisense.ecep.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.implisense.ecep.api.config.EcepConfig;
import com.implisense.ecep.api.config.ElasticsearchConfig;
import com.implisense.ecep.api.config.GeocoderConfig;
import com.implisense.ecep.index.EcepIndex;
import com.implisense.ecep.index.util.ObjectMapperFactory;
import io.dropwizard.setup.Environment;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.net.InetSocketAddress;

public class EcepGuiceModule extends AbstractModule {

    private final EcepConfig configuration;
    private final Environment environment;

    public EcepGuiceModule(EcepConfig configuration, Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
    }

    @Override
    protected void configure() {
        this.bind(EcepConfig.class).toInstance(this.configuration);
        this.bind(ElasticsearchConfig.class).toInstance(this.configuration.getElasticsearch());
        this.bind(GeocoderConfig.class).toInstance(this.configuration.getGeocoder());
    }

    @Provides
    @Singleton
    @Inject
    Client provideElasticsearchClient(ElasticsearchConfig elasticsearchConf) {
        return TransportClient.builder().settings(Settings.settingsBuilder()
                .put("cluster.name", elasticsearchConf.getCluster())
                .put("client.transport.ping_timeout", "60s").build()).build()
                .addTransportAddress(new InetSocketTransportAddress(new InetSocketAddress(
                        elasticsearchConf.getHost(), elasticsearchConf.getPort())));
    }

    @Provides
    @Singleton
    @Inject
    EcepIndex provideCompanyIndex(Client client) throws IOException {
        EcepIndex index = new EcepIndex(client);
        index.createIndex();
        return index;
    }

    @Provides
    @Singleton
    @Inject
    ObjectMapper provideObjectMapper() {
        return ObjectMapperFactory.instance();
    }

}
