package com.implisense.ecep.api.resources;

import com.google.common.base.Charsets;
import com.implisense.ecep.api.data.CompaniesHouseFormatParser;
import com.implisense.ecep.api.data.CompaniesHouseParsingException;
import com.implisense.ecep.api.model.UploadResponse;
import com.implisense.ecep.api.model.UploadResponseStatus;
import com.implisense.ecep.api.model.exceptions.SimpleWebException;
import com.implisense.ecep.api.model.exceptions.WebError;
import com.implisense.ecep.index.EcepIndex;
import com.implisense.ecep.index.model.Company;
import com.implisense.ecep.index.model.ContentField;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

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
    @Path("/companies/multipart")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public UploadResponse processMultipartFormData(@FormDataParam("file") final byte[] input) {
        return importCompanies(input);
    }

    @POST
    @Path("/companies")
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    public UploadResponse processBinaryCompanyData(final byte[] input) {
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

    @POST
    @Path("/content")
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    public UploadResponse processBinaryContentData(final byte[] input) {
        return importContent(input);
    }

    private UploadResponse importContent(byte[] input) {
        final int BULK_SIZE = 100;
        final BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(input), UTF_8));
        final List<String> ids = new ArrayList<>(BULK_SIZE);
        final List<String> texts = new ArrayList<>(BULK_SIZE);
        final List<Company> bulk = new ArrayList<>(BULK_SIZE);
        try {
            int numImported = 0;
            String line;
            while((line = reader.readLine())!=null) {
                int firstTag = line.indexOf("\t");
                ids.add(line.substring(0, firstTag));
                texts.add(line.substring(firstTag + 1));
                if(ids.size() == BULK_SIZE) {
                    List<Company> companies = ecepIndex.getCompanies(ids);
                    for (int i = 0; i < BULK_SIZE; i++) {
                        Company company = companies.get(i);
                        if(company == null) {
                            LOGGER.warn(String.format("No company found for id %s\n", ids.get(i)));
                            continue;
                        }
                        if(!ids.get(i).equals(company.getId())) {
                            throw new RuntimeException("Wrong order of company list!");
                        }
                        if(company.getContent() == null) {
                            company.setContent(new ContentField());
                        }
                        company.getContent().setGeneral(texts.get(i));
                        bulk.add(company);
                    }
                    ecepIndex.putCompanies(bulk);
                    numImported += bulk.size();
                    ids.clear();
                    texts.clear();
                    bulk.clear();
                    LOGGER.info(String.format("%5d imported", numImported));
                }
            }
            if(!ids.isEmpty()) {
                List<Company> companies = ecepIndex.getCompanies(ids);
                for (int i = 0; i < companies.size(); i++) {
                    Company company = companies.get(i);
                    if(!ids.get(i).equals(company.getId())) {
                        throw new RuntimeException("Wrong order of company list!");
                    }
                    if(company.getContent() == null) {
                        company.setContent(new ContentField());
                    }
                    company.getContent().setGeneral(texts.get(i));
                    bulk.add(company);
                }
                ecepIndex.putCompanies(bulk);
                numImported += bulk.size();
                ids.clear();
                texts.clear();
                bulk.clear();
                LOGGER.info(String.format("%5d imported\n", numImported));
            }
            return new UploadResponse(UploadResponseStatus.OK, "Imported " + numImported + " companies.");
        } catch(CompaniesHouseParsingException | IOException e) {
            throw new SimpleWebException(WebError.INVALID_FILE_FORMAT, e.getMessage());
        }
    }
}
