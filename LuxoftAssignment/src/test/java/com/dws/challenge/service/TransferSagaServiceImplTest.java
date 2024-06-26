package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.repository.AccountsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransferSagaServiceImplTest {

    @Mock
    private TransferService transferService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private AccountsRepository accountsRepository;

    @InjectMocks
    private TransferSagaServiceImpl transferSagaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testInitiateTransferSaga_SuccessfulTransfer() throws InsufficientFundsException {
        // Arrange
        String accountFromId = "123";
        String accountToId = "456";
        BigDecimal amount = BigDecimal.valueOf(100);
        Account accountFrom = new Account(accountFromId, BigDecimal.valueOf(500));
        Account accountTo = new Account(accountToId, BigDecimal.valueOf(2000));

        when(accountsRepository.getAccount(accountFromId)).thenReturn(accountFrom);
        when(accountsRepository.getAccount(accountToId)).thenReturn(accountTo);
        //when(transferService.transfer(accountFromId, accountToId, amount)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> transferSagaService.initiateTransferSaga(accountFromId, accountToId, amount));

        // Assert
        verify(accountsRepository, times(1)).getAccount(accountFromId);
        verify(accountsRepository, times(1)).getAccount(accountToId);
        verify(transferService, times(1)).transfer(accountFromId, accountToId, amount);
    }

    @Test
    void testInitiateTransferSaga_InsufficientFunds() throws InsufficientFundsException {
        // Arrange
        String accountFromId = "123";
        String accountToId = "456";
        BigDecimal amount = BigDecimal.valueOf(100);
        Account accountFrom = new Account(accountFromId, BigDecimal.valueOf(50));
        Account accountTo = new Account(accountToId, BigDecimal.valueOf(2000));

        when(accountsRepository.getAccount(accountFromId)).thenReturn(accountFrom);
        when(accountsRepository.getAccount(accountToId)).thenReturn(accountTo);

        verify(notificationService, never()).notifyAboutTransfer(any(Account.class), anyString(), any(BigDecimal.class));
    }

    @Test
    void testInitiateTransferSaga_UnexpectedException() throws InsufficientFundsException {
        // Arrange
        String accountFromId = "123";
        String accountToId = "456";
        BigDecimal amount = BigDecimal.valueOf(100);

        when(accountsRepository.getAccount(accountFromId)).thenThrow(new RuntimeException("Database connection failed"));
        verify(accountsRepository, never()).getAccount(accountToId);
        verify(transferService, never()).transfer(anyString(), anyString(), any(BigDecimal.class));
        verify(notificationService, never()).notifyAboutTransfer(any(Account.class), anyString(), any(BigDecimal.class));
    }
}
