package com.dws.challenge.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DuplicateAccountIdExceptionTest {

    @Test
    void testConstructorWithMessage() {
        // Arrange
        String message = "Test message";

        // Act
        DuplicateAccountIdException exception = new DuplicateAccountIdException(message);

        // Assert
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
}
