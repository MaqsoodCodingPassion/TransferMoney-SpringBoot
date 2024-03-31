package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.exception.TransferSagaException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;


@Service
@Slf4j
public class TransferSagaServiceImpl implements TransferSagaService {

    private final TransferService transferService;
    private final NotificationService notificationService;
    private final AccountsRepository accountsRepository;

    @Autowired
    public TransferSagaServiceImpl(TransferService transferService, NotificationService notificationService, AccountsRepository accountsRepository) {
        this.transferService = transferService;
        this.notificationService = notificationService;
        this.accountsRepository = accountsRepository;
    }

    @Override
    public synchronized void initiateTransferSaga(String accountFromId, String accountToId, BigDecimal amount) throws InsufficientFundsException {
        try {
            // Retrieve accounts
            Account accountFrom = accountsRepository.getAccount(accountFromId);
            Account accountTo = accountsRepository.getAccount(accountToId);

            // Validate accounts and amount
            validateAccounts(accountFrom, accountTo, amount);
            // Perform transfer
            transferService.transfer(accountFromId, accountToId, amount);
            // Send notifications
            notificationService.notifyAboutTransfer(accountFrom, "Transfer to " + accountToId, amount);
            notificationService.notifyAboutTransfer(accountTo, "Transfer from " + accountFromId, amount);

            log.info("Transfer saga completed - Amount: {} transferred from Account {} to Account {}", amount, accountFromId, accountToId);
        } catch (InsufficientFundsException e) {
            // Handle InsufficientFundsException
            log.error("Transfer saga failed - Insufficient funds: {}", e.getMessage());
            // You can throw a custom exception or handle it as required
            throw e;
        } catch (Exception e) {
            // Handle unexpected exceptions
            log.error("Transfer saga failed unexpectedly: {}", e.getMessage());
            // You can throw a custom exception or handle it as required
            throw new TransferSagaException("Transfer failed unexpectedly", e);
        }
    }

    private void validateAccounts(Account accountFrom, Account accountTo, BigDecimal amount) throws InsufficientFundsException {
        if (accountFrom == null || accountTo == null) {
            throw new IllegalArgumentException("Invalid account details provided");
        }
        if (accountFrom.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds in account: " + accountFrom.getAccountId());
        }
    }
}
