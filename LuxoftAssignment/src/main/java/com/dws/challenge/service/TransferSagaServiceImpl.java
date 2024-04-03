package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.exception.TransferSagaException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TransferSagaServiceImpl class responsible for managing the transfer saga.
 * This class implements the TransferSagaService interface.
 */
@Service
@Slf4j
public class TransferSagaServiceImpl implements TransferSagaService {

    private final TransferService transferService;
    private final NotificationService notificationService;
    private final AccountsRepository accountsRepository;
    private final Map<String, Object> accountLocks = new ConcurrentHashMap<>();

    /**
     * Constructs a new TransferSagaServiceImpl with the specified dependencies.
     * @param transferService The service responsible for transferring money between accounts.
     * @param notificationService The service responsible for sending notifications.
     * @param accountsRepository The repository used to retrieve account information.
     */
    @Autowired
    public TransferSagaServiceImpl(TransferService transferService, NotificationService notificationService, AccountsRepository accountsRepository) {
        this.transferService = transferService;
        this.notificationService = notificationService;
        this.accountsRepository = accountsRepository;
    }

    /**
     * Initiates a transfer saga for transferring money between accounts.
     * @param accountFromId The ID of the account from which the transfer is initiated.
     * @param accountToId The ID of the account to which the transfer is made.
     * @param amount The amount of money to transfer.
     * @throws InsufficientFundsException if the account from which the transfer is initiated
     *         does not have sufficient funds to cover the transfer amount.
     * @throws TransferSagaException if the transfer saga fails unexpectedly.
     */
    @Override
    public synchronized void initiateTransferSaga(String accountFromId, String accountToId, BigDecimal amount) throws InsufficientFundsException {
            // Retrieve accounts
            Account accountFrom = accountsRepository.getAccount(accountFromId);
            Account accountTo = accountsRepository.getAccount(accountToId);

            if (accountFrom == null || accountTo == null) {
                throw new IllegalArgumentException("Invalid account details provided");
            }

            // Ensure consistent lock acquisition order to prevent deadlocks
            String lockKey1 = accountFromId.compareTo(accountToId) < 0 ? accountFromId : accountToId;
            String lockKey2 = accountFromId.compareTo(accountToId) < 0 ? accountToId : accountFromId;

            Object lock1 = accountLocks.computeIfAbsent(lockKey1, k -> new Object());
            Object lock2 = accountLocks.computeIfAbsent(lockKey2, k -> new Object());

            synchronized (lock1) {
                synchronized (lock2) {
                    // Perform transfer
                    transferService.transfer(accountFromId, accountToId, amount);

                    // Notify account holders
                    String transferDescription = "Amount transferred to Account " + accountToId + ": " + amount;
                    notificationService.notifyAboutTransfer(accountFrom, transferDescription, amount);
                    notificationService.notifyAboutTransfer(accountTo, transferDescription, amount);
                    log.info("Transfer saga completed - Amount: {} transferred from Account {} to Account {}", amount, accountFromId, accountToId);
                }
          }
    }
}
