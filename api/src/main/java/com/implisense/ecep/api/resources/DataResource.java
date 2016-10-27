package com.implisense.ecep.api.resources;

import com.google.common.base.Charsets;
import com.implisense.ecep.api.data.CompaniesHouseFormatParser;
import com.implisense.ecep.api.data.CompaniesHouseParsingException;
import com.implisense.ecep.api.data.Geocoder;
import com.implisense.ecep.api.data.PostcodeData;
import com.implisense.ecep.api.model.UploadResponse;
import com.implisense.ecep.api.model.UploadResponseStatus;
import com.implisense.ecep.api.model.exceptions.SimpleWebException;
import com.implisense.ecep.api.model.exceptions.WebError;
import com.implisense.ecep.index.EcepIndex;
import com.implisense.ecep.index.EcepIndexException;
import com.implisense.ecep.index.model.Company;
import com.implisense.ecep.index.model.Coordinates;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

@Path("/data")
@Produces(MediaType.APPLICATION_JSON)
public class DataResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataResource.class);
    private final EcepIndex ecepIndex;
    private final CompaniesHouseFormatParser companiesHouseFormatParser;
    private final Geocoder geocoder;

    @Inject
    public DataResource(EcepIndex ecepIndex, CompaniesHouseFormatParser companiesHouseFormatParser, Geocoder geocoder) {
        this.ecepIndex = ecepIndex;
        this.companiesHouseFormatParser = companiesHouseFormatParser;
        this.geocoder = geocoder;
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
        this.geocoder.init();
        final int BULK_SIZE = 1000;
        try {
            int numImported = 0;
            List<Company> bulk = new ArrayList<>(BULK_SIZE);
            for (Company company : companiesHouseFormatParser.iterateCompanies(input, Charsets.US_ASCII)) {
                PostcodeData postcodeData = this.geocoder.lookup(company.getAddress().getPostcode());
                if (postcodeData != null) {
                    Coordinates coordinates = null;
                    if (postcodeData.getLat() != null && postcodeData.getLon() != null) {
                        coordinates = new Coordinates(postcodeData.getLat(), postcodeData.getLon());
                    }
                    company.getAddress().setPostcodeData(new com.implisense.ecep.index.model.PostcodeData(
                            coordinates,
                            postcodeData.getPopulation(),
                            postcodeData.getHouseholds(),
                            postcodeData.getUrbanity()
                    ));
                }
                bulk.add(company);
                if (bulk.size() == BULK_SIZE) {
                    numImported += bulk.size();
                    ecepIndex.putCompanies(bulk);
                    bulk = new ArrayList<>(BULK_SIZE);
                }
            }
            if (!bulk.isEmpty()) {
                numImported += bulk.size();
                ecepIndex.putCompanies(bulk);
            }
            return new UploadResponse(UploadResponseStatus.OK, "Imported " + numImported + " companies.");
        } catch (CompaniesHouseParsingException e) {
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
        final int BULK_SIZE = 500;
        final List<String> lines = new ArrayList<>(BULK_SIZE);
        try {
            final BufferedReader reader = buildReader(input);
            int numImported = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
                if (lines.size() == BULK_SIZE) {
                    ecepIndex.putUrlsWithContent(lines.stream().map(l -> l.split("\t", -1)).toArray(String[][]::new));
                    numImported += lines.size();
                    lines.clear();
                }
            }
            if (!lines.isEmpty()) {
                ecepIndex.putUrlsWithContent(lines.stream().map(l -> l.split("\t", -1)).toArray(String[][]::new));
                numImported += lines.size();
                lines.clear();
            }
            ecepIndex.refresh();
            return new UploadResponse(UploadResponseStatus.OK, "Imported " + numImported + " companies.");
        } catch (EcepIndexException | IOException e) {
            throw new SimpleWebException(WebError.INVALID_FILE_FORMAT, e.getMessage());
        }
    }

    private BufferedReader buildReader(byte[] input) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(input);
        if (isGzipContent(input)) {
            inputStream = new GZIPInputStream(inputStream);
        }
        return new BufferedReader(new InputStreamReader(inputStream, UTF_8));
    }

    private boolean isGzipContent(byte[] input) throws IOException {
        if (input.length < 2) {
            throw new IOException("Empty file!");
        }
        int head = ((int) input[0] & 0xff) | ((input[1] << 8) & 0xff00);
        return (GZIPInputStream.GZIP_MAGIC == head);
    }
}
