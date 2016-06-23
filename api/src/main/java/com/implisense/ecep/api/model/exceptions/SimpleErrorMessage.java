package com.implisense.ecep.api.model.exceptions;

public class SimpleErrorMessage {
    private WebError error;
    private String message;

    public SimpleErrorMessage() {
    }

    public SimpleErrorMessage(WebError error, String message) {
        this.error = error;
        this.message = message;
    }

    public WebError getError() {
        return error;
    }

    public void setError(WebError error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
