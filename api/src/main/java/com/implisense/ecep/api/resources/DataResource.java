package com.implisense.ecep.api.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.implisense.ecep.api.config.EcepConfig;
import com.implisense.ecep.api.format.CompaniesHouseFormatParser;
import com.implisense.ecep.api.format.CompaniesHouseParsingException;
import com.implisense.ecep.api.model.UploadResponse;
import com.implisense.ecep.api.model.UploadResponseStatus;
import com.implisense.ecep.api.model.exceptions.SimpleWebException;
import com.implisense.ecep.api.model.exceptions.WebError;
import com.implisense.ecep.api.util.SicTitleProvider;
import com.implisense.ecep.index.EcepIndex;
import com.implisense.ecep.index.model.Company;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
public class DataResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataResource.class);
    private final EcepIndex ecepIndex;
    private final CompaniesHouseFormatParser companiesHouseFormatParser;

    @Inject
    public DataResource(EcepIndex ecepIndex, CompaniesHouseFormatParser companiesHouseFormatParser) {
        this.ecepIndex = ecepIndex;
        this.companiesHouseFormatParser = companiesHouseFormatParser;
    }

    @POST
    @Path("/multipart")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public UploadResponse processMultipartFormData(@FormDataParam("file") final byte[] input) {
        return importCompanies(input);
    }

    @POST
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    public UploadResponse processBinaryData(final byte[] input) {
        return importCompanies(input);
    }

    private UploadResponse importCompanies(byte[] input) {
        final int BULK_SIZE = 1000;
        try {
            int numImported = 0;
            List<Company> bulk = new ArrayList<>(BULK_SIZE);
            for (Company company : companiesHouseFormatParser.iterateCompanies(input, Charsets.US_ASCII)) {
                bulk.add(company);
                if(bulk.size() == BULK_SIZE) {
                    numImported += bulk.size();
                    ecepIndex.putCompanies(bulk);
                    bulk = new ArrayList<>(BULK_SIZE);
                }
            }
            if(!bulk.isEmpty()) {
                numImported += bulk.size();
                ecepIndex.putCompanies(bulk);
            }
            return new UploadResponse(UploadResponseStatus.OK, "Imported " + numImported + " companies.");
        } catch(CompaniesHouseParsingException e) {
            throw new SimpleWebException(WebError.INVALID_FILE_FORMAT, e.getMessage());
        }
    }
}
