package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.exception.TransferSagaException;
import com.dws.challenge.repository.AccountsRepository;
import com.dws.challenge.service.NotificationService;
import com.dws.challenge.service.TransferService;
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
        verify(notificationService, times(1)).notifyAboutTransfer(accountFrom, "Transfer to " + accountToId, amount);
        verify(notificationService, times(1)).notifyAboutTransfer(accountTo, "Transfer from " + accountFromId, amount);
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
       /* when(transferService.transfer(accountFromId, accountToId, amount))
                .thenThrow(new InsufficientFundsException("Insufficient funds"));*/

        // Act and Assert
        assertThrows(InsufficientFundsException.class,
                () -> transferSagaService.initiateTransferSaga(accountFromId, accountToId, amount));

        verify(accountsRepository, times(1)).getAccount(accountFromId);
        verify(accountsRepository, times(1)).getAccount(accountToId);
        verify(notificationService, never()).notifyAboutTransfer(any(Account.class), anyString(), any(BigDecimal.class));
    }

    @Test
    void testInitiateTransferSaga_UnexpectedException() throws InsufficientFundsException {
        // Arrange
        String accountFromId = "123";
        String accountToId = "456";
        BigDecimal amount = BigDecimal.valueOf(100);

        when(accountsRepository.getAccount(accountFromId)).thenThrow(new RuntimeException("Database connection failed"));

        // Act and Assert
        TransferSagaException exception = assertThrows(TransferSagaException.class,
                () -> transferSagaService.initiateTransferSaga(accountFromId, accountToId, amount));
        assertEquals("Transfer failed unexpectedly", exception.getMessage());

        verify(accountsRepository, times(1)).getAccount(accountFromId);
        verify(accountsRepository, never()).getAccount(accountToId);
        verify(transferService, never()).transfer(anyString(), anyString(), any(BigDecimal.class));
        verify(notificationService, never()).notifyAboutTransfer(any(Account.class), anyString(), any(BigDecimal.class));
    }
}
