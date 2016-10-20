package com.implisense.ecep.index.model;

public class PostcodeData {
    private Coordinates coordinates;
    private Integer population;
    private Integer households;
    private String urbanity;

    public PostcodeData() {
    }

    public PostcodeData(Coordinates coordinates, int population, int households, String urbanity) {
        this.coordinates = coordinates;
        this.population = population;
        this.households = households;
        this.urbanity = urbanity;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public int getHouseholds() {
        return households;
    }

    public void setHouseholds(int households) {
        this.households = households;
    }

    public String getUrbanity() {
        return urbanity;
    }

    public void setUrbanity(String urbanity) {
        this.urbanity = urbanity;
    }
}
