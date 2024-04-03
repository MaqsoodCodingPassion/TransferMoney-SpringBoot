package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * TransferServiceImpl class responsible for transferring money between accounts.
 * This class implements the TransferService interface.
 */
@Service
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final AccountsRepository accountsRepository;

    /**
     * Constructs a new TransferServiceImpl with the specified AccountsRepository.
     * @param accountsRepository The repository used to retrieve account information.
     */
    @Autowired
    public TransferServiceImpl(AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    /**
     * Performs a money transfer from one account to another.
     * @param accountFromId The ID of the account from which the transfer is initiated.
     * @param accountToId The ID of the account to which the transfer is made.
     * @param amount The amount of money to transfer.
     * @throws InsufficientFundsException if the account from which the transfer is initiated
     *         does not have sufficient funds to cover the transfer amount.
     */
    @Override
    @Transactional
    public void transfer(String accountFromId, String accountToId, BigDecimal amount) throws InsufficientFundsException {
        synchronized (this) {
            Account accountFrom = accountsRepository.getAccount(accountFromId);
            Account accountTo = accountsRepository.getAccount(accountToId);

            if (accountFrom == null || accountTo == null) {
                throw new IllegalArgumentException("Invalid account details provided");
            }
            if (accountFrom.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException("Insufficient funds in account: " + accountFrom.getAccountId());
            }

            // Perform transfer
            accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
            accountTo.setBalance(accountTo.getBalance().add(amount));

            log.info("Transfer completed - Amount: {} transferred from Account {} to Account {}", amount, accountFromId, accountToId);
        }
    }
}
