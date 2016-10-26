package com.implisense.ecep.api.model;

public class PostcodeIndustryItem {
    private String postCode;
    private String sicCode;
    private String sicTitle;
    private int result;
    private int total;

    public PostcodeIndustryItem() {
    }

    public PostcodeIndustryItem(String postCode, String sicCode, String sicTitle, int result, int total) {
        this.postCode = postCode;
        this.sicCode = sicCode;
        this.sicTitle = sicTitle;
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

    public String getSicTitle() {
        return sicTitle;
    }

    public void setSicTitle(String sicTitle) {
        this.sicTitle = sicTitle;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
