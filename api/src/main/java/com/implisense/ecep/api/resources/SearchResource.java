package com.implisense.ecep.api.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.implisense.ecep.api.config.EcepConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/merge")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchResource.class);
    private final EcepConfig config;
    private final ObjectMapper objectMapper;

    @Inject
    public SearchResource(EcepConfig config, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String test() {
        return "test";
    }
}
