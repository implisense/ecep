package com.implisense.ecep.index.model;

import java.util.Date;

public class PreviousName {
    private Date changeDate;
    private String name;

    public PreviousName() {
    }

    public PreviousName(Date changeDate, String name) {
        this.changeDate = changeDate;
        this.name = name;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
