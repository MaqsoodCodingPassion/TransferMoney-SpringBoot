package com.dws.challenge.service;

import com.dws.challenge.domain.Account;

import java.math.BigDecimal;

public interface NotificationService {

  void notifyAboutTransfer(Account account, String transferDescription,BigDecimal amount);
}
