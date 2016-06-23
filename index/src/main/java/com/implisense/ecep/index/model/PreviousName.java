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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PreviousName that = (PreviousName) o;

        if (changeDate != null ? !changeDate.equals(that.changeDate) : that.changeDate != null) return false;
        return name != null ? name.equals(that.name) : that.name == null;

    }

    @Override
    public int hashCode() {
        int result = changeDate != null ? changeDate.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
