package com.implisense.ecep.api.data;

public class CompaniesHouseParsingException extends RuntimeException {
    public CompaniesHouseParsingException() {
    }

    public CompaniesHouseParsingException(String message) {
        super(message);
    }

    public CompaniesHouseParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompaniesHouseParsingException(Throwable cause) {
        super(cause);
    }
}
