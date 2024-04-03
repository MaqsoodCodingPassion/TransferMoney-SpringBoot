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

public class TransferServiceImplTest {

    @Mock
    private AccountsRepository accountsRepository;

    @InjectMocks
    private TransferServiceImpl transferService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testTransfer_SuccessfulTransfer() throws InsufficientFundsException {
        // Arrange
        String accountFromId = "123";
        String accountToId = "456";
        BigDecimal amount = BigDecimal.valueOf(100);
        Account accountFrom = new Account(accountFromId, BigDecimal.valueOf(500));
        Account accountTo = new Account(accountToId, BigDecimal.valueOf(2000));

        when(accountsRepository.getAccount(accountFromId)).thenReturn(accountFrom);
        when(accountsRepository.getAccount(accountToId)).thenReturn(accountTo);

        // Act
        assertDoesNotThrow(() -> transferService.transfer(accountFromId, accountToId, amount));

        // Assert
        assertEquals(BigDecimal.valueOf(400), accountFrom.getBalance());
        assertEquals(BigDecimal.valueOf(2100), accountTo.getBalance());
    }

    @Test
    void testTransfer_InsufficientFunds() {
        // Arrange
        String accountFromId = "123";
        String accountToId = "456";
        BigDecimal amount = BigDecimal.valueOf(100);
        Account accountFrom = new Account(accountFromId, BigDecimal.valueOf(50));
        Account accountTo = new Account(accountToId, BigDecimal.valueOf(2000));

        when(accountsRepository.getAccount(accountFromId)).thenReturn(accountFrom);
        when(accountsRepository.getAccount(accountToId)).thenReturn(accountTo);

        // Act and Assert
        InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
                () -> transferService.transfer(accountFromId, accountToId, amount));

        assertEquals("Insufficient funds in account: 123", exception.getMessage());
        assertEquals(BigDecimal.valueOf(50), accountFrom.getBalance());
        assertEquals(BigDecimal.valueOf(2000), accountTo.getBalance());
    }

    @Test
    void testTransfer_NullAccount() {
        // Arrange
        String accountFromId = "123";
        String accountToId = "456";
        BigDecimal amount = BigDecimal.valueOf(100);
        Account accountTo = new Account(accountToId, BigDecimal.valueOf(2000));

        when(accountsRepository.getAccount(accountFromId)).thenReturn(null);
        when(accountsRepository.getAccount(accountToId)).thenReturn(accountTo);

        // Act and Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transferService.transfer(accountFromId, accountToId, amount));

        assertEquals("Invalid account details provided", exception.getMessage());
    }

    @Test
    public void testTransfer_NegativeAmount() {
        // Create test accounts
        Account accountFrom = new Account("123", new BigDecimal("1000.00"));
        Account accountTo = new Account("456", new BigDecimal("500.00"));

        // Attempt to transfer a negative amount
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferService.transfer(accountFrom.getAccountId(), accountTo.getAccountId(), new BigDecimal("-100"));
        });

        // Verify that the expected exception is thrown
        assertEquals("Invalid account details provided", exception.getMessage());
    }
}
