package com.implisense.ecep.api.model;

public class HeatmapPoint {
    private double lat;
    private double lon;
    private double value;
    private String postcode;

    public HeatmapPoint() {
    }

    public HeatmapPoint(double lat, double lon, double value, String postcode) {
        this.lat = lat;
        this.lon = lon;
        this.value = value;
        this.postcode = postcode;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
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
