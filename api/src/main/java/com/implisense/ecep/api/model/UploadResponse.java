package com.implisense.ecep.api.model;

public class UploadResponse {
    private UploadResponseStatus status;
    private String message;

    public UploadResponse(UploadResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public UploadResponseStatus getStatus() {
        return status;
    }

    public void setStatus(UploadResponseStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
