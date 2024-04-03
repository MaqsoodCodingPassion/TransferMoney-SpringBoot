package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.exception.TransferSagaException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

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
        synchronized (this) {
            // Retrieve accounts
            Account accountFrom = accountsRepository.getAccount(accountFromId);
            Account accountTo = accountsRepository.getAccount(accountToId);

            // Perform transfer
            transferService.transfer(accountFromId, accountToId, amount);
            // Send notifications
            notificationService.notifyAboutTransfer(accountFrom, "Transfer to " + accountToId, amount);
            notificationService.notifyAboutTransfer(accountTo, "Transfer from " + accountFromId, amount);

            log.info("Transfer saga completed - Amount: {} transferred from Account {} to Account {}", amount, accountFromId, accountToId);
         }
    }
}
