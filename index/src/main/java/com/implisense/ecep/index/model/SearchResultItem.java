package com.implisense.ecep.index.model;

public class SearchResultItem {
    private String postcode;
    private String sicCode;
    private long result;
    private long total;

    public SearchResultItem(String postcode, String sicCode, long result, long total) {
        this.postcode = postcode;
        this.sicCode = sicCode;
        this.result = result;
        this.total = total;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getSicCode() {
        return sicCode;
    }

    public void setSicCode(String sicCode) {
        this.sicCode = sicCode;
    }

    public long getResult() {
        return result;
    }

    public void setResult(long result) {
        this.result = result;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "SearchResultItem{" +
                "postcode='" + postcode + '\'' +
                ", sicCode='" + sicCode + '\'' +
                ", result=" + result +
                ", total=" + total +
                '}';
    }
}
