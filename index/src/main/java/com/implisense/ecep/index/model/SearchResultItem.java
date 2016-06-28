package com.implisense.ecep.index.model;

public class SearchResultItem {
    private String postCode;
    private String sicCode;
    private long result;
    private long total;

    public SearchResultItem(String postCode, String sicCode, long result, long total) {
        this.postCode = postCode;
        this.sicCode = sicCode;
        this.result = result;
        this.total = total;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
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
                "postCode='" + postCode + '\'' +
                ", sicCode='" + sicCode + '\'' +
                ", result=" + result +
                ", total=" + total +
                '}';
    }
}
