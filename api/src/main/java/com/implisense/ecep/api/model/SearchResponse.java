package com.implisense.ecep.api.model;

import java.util.List;

public class SearchResponse {
    private SearchRequest request;
    private List<SearchResponseItem> results;
    private HeatmapData heatmap;
    private List<String> correlatedTerms;

    public SearchResponse() {
    }

    public SearchResponse(SearchRequest request, List<SearchResponseItem> results, HeatmapData heatmap, List<String> correlatedTerms) {
        this.request = request;
        this.results = results;
        this.heatmap = heatmap;
        this.correlatedTerms = correlatedTerms;
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

    public List<String> getCorrelatedTerms() {
        return correlatedTerms;
    }

    public void setCorrelatedTerms(List<String> correlatedTerms) {
        this.correlatedTerms = correlatedTerms;
    }
}
