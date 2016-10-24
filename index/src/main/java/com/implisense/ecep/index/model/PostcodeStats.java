package com.implisense.ecep.index.model;

import java.util.List;

public class PostcodeStats {
    private List<PostcodeBucket> absolute;
    private List<PostcodeBucket> relative;

    public PostcodeStats(List<PostcodeBucket> absolute, List<PostcodeBucket> relative) {
        this.absolute = absolute;
        this.relative = relative;
    }

    public List<PostcodeBucket> getAbsolute() {
        return absolute;
    }

    public void setAbsolute(List<PostcodeBucket> absolute) {
        this.absolute = absolute;
    }

    public List<PostcodeBucket> getRelative() {
        return relative;
    }

    public void setRelative(List<PostcodeBucket> relative) {
        this.relative = relative;
    }
}
