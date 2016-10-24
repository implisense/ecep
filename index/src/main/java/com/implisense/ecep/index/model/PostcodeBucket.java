package com.implisense.ecep.index.model;

public class PostcodeBucket {
    private double value;
    private String postcode;

    public PostcodeBucket(double value, String postcode) {
        this.value = value;
        this.postcode = postcode;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
