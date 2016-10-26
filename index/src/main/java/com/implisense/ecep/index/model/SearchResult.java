package com.implisense.ecep.index.model;

import java.util.List;

public class SearchResult {
    private long numHits;
    private List<Company> topHits;
    private List<PostcodeIndustryItem> postcodeIndustryDistribution;
    private PostcodeStats postcodeStats;
    private List<String> correlatedTerms;

    public SearchResult() {
    }

    public SearchResult(long numHits, List<Company> topHits, List<PostcodeIndustryItem> postcodeIndustryDistribution,
                        PostcodeStats postcodeStats, List<String> correlatedTerms) {
        this.numHits = numHits;
        this.topHits = topHits;
        this.postcodeIndustryDistribution = postcodeIndustryDistribution;
        this.postcodeStats = postcodeStats;
        this.correlatedTerms = correlatedTerms;
    }

    public long getNumHits() {
        return numHits;
    }

    public void setNumHits(long numHits) {
        this.numHits = numHits;
    }

    public List<Company> getTopHits() {
        return topHits;
    }

    public void setTopHits(List<Company> topHits) {
        this.topHits = topHits;
    }

    public List<PostcodeIndustryItem> getPostcodeIndustryDistribution() {
        return postcodeIndustryDistribution;
    }

    public void setPostcodeIndustryDistribution(List<PostcodeIndustryItem> postcodeIndustryDistribution) {
        this.postcodeIndustryDistribution = postcodeIndustryDistribution;
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
