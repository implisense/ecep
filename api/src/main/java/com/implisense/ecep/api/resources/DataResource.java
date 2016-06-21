package com.implisense.ecep.api.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.implisense.ecep.api.config.EcepConfig;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
public class DataResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataResource.class);
    private final EcepConfig config;
    private final ObjectMapper objectMapper;

    @Inject
    public DataResource(EcepConfig config, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public String processMultipartFormData(@FormDataParam("file") final byte[] input) {
        return "test";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String processBinaryData(final byte[] input) {
        return "test";
    }
}
