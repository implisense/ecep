package com.implisense.ecep.api.model;

import com.implisense.ecep.index.model.Company;

import java.util.List;

public class SearchResponse {
    private SearchRequest request;
    private List<Company> topHits;
    private List<PostcodeIndustryItem> postcodeIndustryDistribution;
    private HeatmapData heatmap;
    private List<String> correlatedTerms;

    public SearchResponse() {
    }

    public SearchResponse(SearchRequest request, List<Company> topHits,
                          List<PostcodeIndustryItem> postcodeIndustryDistribution, HeatmapData heatmap,
                          List<String> correlatedTerms) {
        this.request = request;
        this.topHits = topHits;
        this.postcodeIndustryDistribution = postcodeIndustryDistribution;
        this.heatmap = heatmap;
        this.correlatedTerms = correlatedTerms;
    }

    public SearchRequest getRequest() {
        return request;
    }

    public void setRequest(SearchRequest request) {
        this.request = request;
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
