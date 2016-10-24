package com.implisense.ecep.api.resources;

import com.implisense.ecep.api.data.Geocoder;
import com.implisense.ecep.api.data.PostcodeData;
import com.implisense.ecep.api.data.Sic07TitleProvider;
import com.implisense.ecep.api.model.*;
import com.implisense.ecep.index.EcepIndex;
import com.implisense.ecep.index.model.PostcodeBucket;
import com.implisense.ecep.index.model.PostcodeStats;
import com.implisense.ecep.index.model.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Path("/search")
@Produces(MediaType.APPLICATION_JSON)
public class SearchResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchResource.class);
    private final EcepIndex ecepIndex;
    private final Sic07TitleProvider sic07TitleProvider;
    private final Geocoder geocoder;

    @Inject
    public SearchResource(EcepIndex ecepIndex, Sic07TitleProvider sic07TitleProvider, Geocoder geocoder) {
        this.ecepIndex = ecepIndex;
        this.sic07TitleProvider = sic07TitleProvider;
        this.geocoder = geocoder;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public SearchResponse search(SearchRequest request) {
        this.geocoder.init();
        SearchResult result = this.ecepIndex.search(request.getQuery(), request.getPostCode(),
                request.getSicCode(), request.getCategory());
        SearchResponse response = new SearchResponse(request, result.getItems().stream()
                .map(i -> new SearchResponseItem(i.getPostcode(), i.getSicCode(),
                        this.sic07TitleProvider.getTitle(i.getSicCode()),
                        (int) i.getResult(), (int) i.getTotal()))
                .collect(toList()), convert(result.getPostcodeStats()), result.getSignificantTerms());
        return response;
    }

    private HeatmapData convert(PostcodeStats input) {
        return new HeatmapData(
                input.getAbsolute().stream()
                        .map(this::toHeatmapPoint)
                        .filter(Objects::nonNull)
                        .limit(1000)
                        .collect(toList()),
                input.getRelative().stream()
                        .map(this::toHeatmapPoint)
                        .filter(Objects::nonNull)
                        .limit(1000)
                        .collect(toList())
        );
    }

    private HeatmapPoint toHeatmapPoint(PostcodeBucket bucket) {
        PostcodeData stats = this.geocoder.lookup(bucket.getPostcode());
        return stats == null ? null :
                new HeatmapPoint(stats.getLat(), stats.getLon(), bucket.getValue(), bucket.getPostcode());
    }
}
