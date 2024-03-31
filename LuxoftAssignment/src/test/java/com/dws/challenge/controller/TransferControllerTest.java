package com.dws.challenge.controller;

import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.service.TransferSagaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class TransferControllerTest {

    @Mock
    private TransferSagaService transferSagaService;

    @InjectMocks
    private TransferController transferController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInitiateTransfer_Success() throws InsufficientFundsException {
        // Arrange
        String accountFromId = "123";
        String accountToId = "456";
        BigDecimal amount = BigDecimal.valueOf(100);

        // Act
        ResponseEntity<String> responseEntity = transferController.initiateTransfer(accountFromId, accountToId, amount);

        // Assert
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Transfer initiated successfully", responseEntity.getBody());
        verify(transferSagaService, times(1)).initiateTransferSaga(accountFromId, accountToId, amount);
    }

    @Test
    void testInitiateTransfer_InsufficientFunds() throws InsufficientFundsException {
        // Arrange
        String accountFromId = "123";
        String accountToId = "456";
        BigDecimal amount = BigDecimal.valueOf(100);

        doThrow(new InsufficientFundsException("Insufficient funds")).when(transferSagaService)
                .initiateTransferSaga(accountFromId, accountToId, amount);

        // Act
        ResponseEntity<String> responseEntity = transferController.initiateTransfer(accountFromId, accountToId, amount);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Insufficient funds", responseEntity.getBody());
        verify(transferSagaService, times(1)).initiateTransferSaga(accountFromId, accountToId, amount);
    }

    @Test
    void testInitiateTransfer_InternalServerError() throws InsufficientFundsException {
        // Arrange
        String accountFromId = "123";
        String accountToId = "456";
        BigDecimal amount = BigDecimal.valueOf(100);

        doThrow(new RuntimeException("Internal server error")).when(transferSagaService)
                .initiateTransferSaga(accountFromId, accountToId, amount);

        // Act
        ResponseEntity<String> responseEntity = transferController.initiateTransfer(accountFromId, accountToId, amount);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Transfer failed: Internal server error", responseEntity.getBody());
        verify(transferSagaService, times(1)).initiateTransferSaga(accountFromId, accountToId, amount);
    }
}
