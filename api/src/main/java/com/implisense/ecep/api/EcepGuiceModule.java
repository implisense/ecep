package com.implisense.ecep.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.implisense.ecep.api.config.EcepConfig;
import com.implisense.ecep.api.config.ElasticsearchConfig;
import io.dropwizard.setup.Environment;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.util.Locale;
import java.util.TimeZone;

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
    ObjectMapper provideObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        DateFormat dateFormat = StdDateFormat.getISO8601Format(TimeZone.getTimeZone("Europe/London"), Locale.ENGLISH);
        objectMapper.setDateFormat(dateFormat);
        return objectMapper;
    }

}
