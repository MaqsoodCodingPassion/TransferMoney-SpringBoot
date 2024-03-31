package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final AccountsRepository accountsRepository;

    @Autowired
    public TransferServiceImpl(AccountsRepository accountsRepository) {
        this.accountsRepository = accountsRepository;
    }

    @Override
    @Transactional
    public void transfer(String accountFromId, String accountToId, BigDecimal amount) throws InsufficientFundsException {
        // Retrieve accounts
        Account accountFrom = accountsRepository.getAccount(accountFromId);
        Account accountTo = accountsRepository.getAccount(accountToId);

        // Validate accounts and amount
        validateAccounts(accountFrom, accountTo, amount);

        // Perform transfer
        accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
        accountTo.setBalance(accountTo.getBalance().add(amount));

        log.info("Transfer completed - Amount: {} transferred from Account {} to Account {}", amount, accountFromId, accountToId);
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



