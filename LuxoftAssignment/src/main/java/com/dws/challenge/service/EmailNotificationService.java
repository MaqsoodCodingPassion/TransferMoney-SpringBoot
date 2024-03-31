package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class EmailNotificationService implements NotificationService {

  @Override
  public void notifyAboutTransfer(Account account, String transferDescription, BigDecimal amount) {
    log.info("Sending notification to owner of {}: Transfer of {} to another account. Amount: {}", account.getAccountId(), transferDescription, amount);
    // Implementation to send email notification
  }
}

