package com.implisense.ecep.api.data;

public class PostcodeData {
    private Double lat;
    private Double lon;
    private Integer population;
    private Integer households;
    private String urbanity;

    public PostcodeData() {
    }

    public PostcodeData(Double lat, Double lon, Integer population, Integer households, String urbanity) {
        this.lat = lat;
        this.lon = lon;
        this.population = population;
        this.households = households;
        this.urbanity = urbanity;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public Integer getHouseholds() {
        return households;
    }

    public void setHouseholds(Integer households) {
        this.households = households;
    }

    public String getUrbanity() {
        return urbanity;
    }

    public void setUrbanity(String urbanity) {
        this.urbanity = urbanity;
    }
}
