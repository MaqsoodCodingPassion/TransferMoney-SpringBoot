package com.dws.challenge.exception;

public class TransferSagaException extends RuntimeException {

    public TransferSagaException(String message) {
        super(message);
    }

    public TransferSagaException(String message, Throwable cause) {
        super(message, cause);
    }
}

