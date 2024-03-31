package com.dws.challenge.service;

import com.dws.challenge.exception.InsufficientFundsException;

import java.math.BigDecimal;

public interface TransferService {
    void transfer(String accountFromId, String accountToId, BigDecimal amount) throws InsufficientFundsException;
}

