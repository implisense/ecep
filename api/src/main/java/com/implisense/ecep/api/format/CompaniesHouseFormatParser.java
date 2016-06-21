package com.implisense.ecep.api.format;

public class CompaniesHouseFormatParser {

    private int NAME = -1;
    private int NUMBER = -1;
    private int ADDR_CAREOF = -1;
    private int ADDR_POBOX = -1;
    private int ADDR_LINE1 = -1;
    private int ADDR_LINE2 = -1;
    private int ADDR_TOWN = -1;
    private int ADDR_COUNTY = -1;
    private int ADDR_COUNTRY = -1;
    private int ADDR_POSTCODE = -1;
    private int CATEGORY = -1;
    private int STATUS = -1;
    private int COUNTRYOFORIGIN = -1;
    private int DISSOLUTIONDATE = -1;
    private int INCORPORATIONDATE = -1;
    private int URI = -1;
    private int SICCODESSTART = -1;
    private int SICCODESEND = -1;
    private int PREVIOUSNAMESSTART = -1;
    private int PREVIOUSNAMESEND = -1;

    public CompaniesHouseFormatParser(String[] header) {
        this.parseHeader(header);
    }

    private void parseHeader(String[] header) {
        for (int i = 0; i < header.length; i++) {
            switch (header[i]) {
                case "CompanyName":
                    this.NAME = i;
                    break;
                case "CompanyNumber":
                    this.NUMBER = i;
                    break;
                case "RegAddress.CareOf":
                    this.ADDR_CAREOF = i;
                    break;
                case "RegAddress.POBox":
                    this.ADDR_POBOX = i;
                    break;
                case "RegAddress.AddressLine1":
                    this.ADDR_LINE1 = i;
                    break;
                case "RegAddress.AddressLine2":
                    this.ADDR_LINE2 = i;
                    break;
                case "RegAddress.PostTown":
                    this.ADDR_TOWN = i;
                    break;
                case "RegAddress.County":
                    this.ADDR_COUNTY = i;
                    break;
                case "RegAddress.Country":
                    this.ADDR_COUNTRY = i;
                    break;
                case "RegAddress.PostCode":
                    this.ADDR_POSTCODE = i;
                    break;
                case "CompanyCategory":
                    this.CATEGORY = i;
                    break;
                case "CompanyStatus":
                    this.STATUS = i;
                    break;
                case "CountryOfOrigin":
                    this.COUNTRYOFORIGIN = i;
                    break;
                case "DissolutionDate":
                    this.DISSOLUTIONDATE = i;
                    break;
                case "IncorporationDate":
                    this.INCORPORATIONDATE = i;
                    break;
                case "URI":
                    this.URI = i;
                    break;
            }
            if (header[i].startsWith("SICCode")) {
                if (SICCODESSTART == -1) {
                    SICCODESSTART = i;
                }
            } else if(SICCODESSTART != -1 && SICCODESEND == -1) {
                SICCODESEND = i;
            }
            if (header[i].startsWith("PreviousName")) {
                if (PREVIOUSNAMESSTART == -1) {
                    PREVIOUSNAMESSTART = i;
                }
            } else if(PREVIOUSNAMESSTART != -1 && PREVIOUSNAMESEND == -1) {
                PREVIOUSNAMESEND = i;
            }
        }
    }
}
