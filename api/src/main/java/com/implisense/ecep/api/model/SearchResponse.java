package com.implisense.ecep.api.model;

import java.util.List;

public class SearchResponse {
    private SearchRequest request;
    private List<SearchResponseItem> results;
    private HeatmapData heatmap;

    public SearchResponse() {
    }

    public SearchResponse(SearchRequest request, List<SearchResponseItem> results, HeatmapData heatmap) {
        this.request = request;
        this.results = results;
        this.heatmap = heatmap;
    }

    public SearchRequest getRequest() {
        return request;
    }

    public void setRequest(SearchRequest request) {
        this.request = request;
    }

    public List<SearchResponseItem> getResults() {
        return results;
    }

    public void setResults(List<SearchResponseItem> results) {
        this.results = results;
    }

    public HeatmapData getHeatmap() {
        return heatmap;
    }

    public void setHeatmap(HeatmapData heatmap) {
        this.heatmap = heatmap;
    }
}
