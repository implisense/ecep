package com.implisense.ecep.api.config;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

public class ElasticsearchConfig {

    @NotEmpty
    private String host = "localhost";

    @Min(1)
    @Max(65535)
    private int port = 9300;

    @NotEmpty
    private String cluster = "ecep";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }
}
