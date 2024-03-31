package com.dws.challenge.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class InsufficientFundsExceptionTest {

    @Test
    void testConstructorWithMessage() {
        // Arrange
        String message = "Test message";

        // Act
        InsufficientFundsException exception = new InsufficientFundsException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
}
