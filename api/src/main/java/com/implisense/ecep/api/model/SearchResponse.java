package com.implisense.ecep.api.model;

import java.util.List;

public class SearchResponse {
    private SearchRequest request;
    private List<SearchResponseItem> results;

    public SearchResponse() {
    }

    public SearchResponse(SearchRequest request, List<SearchResponseItem> results) {
        this.request = request;
        this.results = results;
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
}
