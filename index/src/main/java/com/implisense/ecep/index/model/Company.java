package com.implisense.ecep.index.model;

import java.util.Date;
import java.util.List;

public class Company {
    private String id;
    private String uri;
    private String name;
    private Address address;
    private String category;
    private String status;
    private String countryOfOrigin;
    private Date incorporationDate;
    private Date dissolutionDate;
    private String phone;
    private String fax;
    private String email;
    private String url;
    private ExternalIds externalIds;
    private ContentField content;
    private List<String> sicCodes;
    private List<PreviousName> previousNames;
    private Date timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public Date getIncorporationDate() {
        return incorporationDate;
    }

    public void setIncorporationDate(Date incorporationDate) {
        this.incorporationDate = incorporationDate;
    }

    public Date getDissolutionDate() {
        return dissolutionDate;
    }

    public void setDissolutionDate(Date dissolutionDate) {
        this.dissolutionDate = dissolutionDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ExternalIds getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(ExternalIds externalIds) {
        this.externalIds = externalIds;
    }

    public ContentField getContent() {
        return content;
    }

    public void setContent(ContentField content) {
        this.content = content;
    }

    public List<String> getSicCodes() {
        return sicCodes;
    }

    public void setSicCodes(List<String> sicCodes) {
        this.sicCodes = sicCodes;
    }

    public List<PreviousName> getPreviousNames() {
        return previousNames;
    }

    public void setPreviousNames(List<PreviousName> previousNames) {
        this.previousNames = previousNames;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
