package com.implisense.ecep.api.model.exceptions;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class SimpleWebException extends WebApplicationException {
    public SimpleWebException(WebError error, String message) {
        super(Response.status(getStatus(error)).entity(new SimpleErrorMessage(error, message)).build());
    }

    private static Response.Status getStatus(WebError error) {
        return error == WebError.NOT_FOUND ? Response.Status.NOT_FOUND : Response.Status.BAD_REQUEST;
    }


}
