package com.implisense.ecep.api.model;

import java.util.List;

public class HeatmapData {
    private List<HeatmapPoint> absolute;
    private List<HeatmapPoint> relative;

    public HeatmapData() {
    }

    public HeatmapData(List<HeatmapPoint> absolute, List<HeatmapPoint> relative) {
        this.absolute = absolute;
        this.relative = relative;
    }

    public List<HeatmapPoint> getAbsolute() {
        return absolute;
    }

    public void setAbsolute(List<HeatmapPoint> absolute) {
        this.absolute = absolute;
    }

    public List<HeatmapPoint> getRelative() {
        return relative;
    }

    public void setRelative(List<HeatmapPoint> relative) {
        this.relative = relative;
    }
}
