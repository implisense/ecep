package com.implisense.ecep.api.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.implisense.ecep.api.data.Sic07TitleProvider;
import com.implisense.ecep.api.model.SearchRequest;
import com.implisense.ecep.api.model.SearchResponse;
import com.implisense.ecep.api.model.SearchResponseItem;
import com.implisense.ecep.index.EcepIndex;
import com.implisense.ecep.index.model.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.stream.Collectors;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchResource.class);
    private final EcepIndex ecepIndex;
    private final Sic07TitleProvider sic07TitleProvider;

    @Inject
    public SearchResource(EcepIndex ecepIndex, Sic07TitleProvider sic07TitleProvider) {
        this.ecepIndex = ecepIndex;
        this.sic07TitleProvider = sic07TitleProvider;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResponse search(SearchRequest request) {
        SearchResult result = this.ecepIndex.search(request.getQuery(), request.getPostCode(),
                request.getSicCode(), request.getCategory());
        SearchResponse response = new SearchResponse(request, result.getItems().stream()
                .map(i -> new SearchResponseItem(i.getPostCode(), i.getSicCode(),
                        this.sic07TitleProvider.getTitle(i.getSicCode()),
                        (int) i.getResult(), (int) i.getTotal()))
                .collect(Collectors.toList()));
        return response;
    }
}
