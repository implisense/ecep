package com.implisense.ecep.index.model;

import java.util.List;

public class SearchResult {
    private long numHits;
    private List<SearchResultItem> items;
    private PostcodeStats postcodeStats;
    private List<String> correlatedTerms;

    public SearchResult() {
    }

    public SearchResult(long numHits, List<SearchResultItem> items, PostcodeStats postcodeStats, List<String> correlatedTerms) {
        this.numHits = numHits;
        this.items = items;
        this.postcodeStats = postcodeStats;
        this.correlatedTerms = correlatedTerms;
    }

    public long getNumHits() {
        return numHits;
    }

    public void setNumHits(long numHits) {
        this.numHits = numHits;
    }

    public List<SearchResultItem> getItems() {
        return items;
    }

    public void setItems(List<SearchResultItem> items) {
        this.items = items;
    }

    public PostcodeStats getPostcodeStats() {
        return postcodeStats;
    }

    public void setPostcodeStats(PostcodeStats postcodeStats) {
        this.postcodeStats = postcodeStats;
    }

    public List<String> getCorrelatedTerms() {
        return correlatedTerms;
    }

    public void setCorrelatedTerms(List<String> correlatedTerms) {
        this.correlatedTerms = correlatedTerms;
    }
}
