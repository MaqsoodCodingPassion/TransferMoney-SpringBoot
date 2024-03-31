package com.dws.challenge.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class TransferSagaExceptionTest {

    @Test
    void testConstructorWithMessage() {
        // Arrange
        String message = "Test message";

        // Act
        TransferSagaException exception = new TransferSagaException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        // Arrange
        String message = "Test message";
        Throwable cause = new RuntimeException("Cause of exception");

        // Act
        TransferSagaException exception = new TransferSagaException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
