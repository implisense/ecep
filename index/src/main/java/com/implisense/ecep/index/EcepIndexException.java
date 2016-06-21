package com.implisense.ecep.index;

public class EcepIndexException extends RuntimeException {
    public EcepIndexException() {
    }

    public EcepIndexException(String message) {
        super(message);
    }

    public EcepIndexException(String message, Throwable cause) {
        super(message, cause);
    }

    public EcepIndexException(Throwable cause) {
        super(cause);
    }

    public EcepIndexException(String message, Throwable cause, boolean enableSuppression, boolean
            writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
