package com.dws.challenge.controller;

import com.dws.challenge.exception.InsufficientFundsException;
import com.dws.challenge.service.TransferSagaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferSagaService transferSagaService;

    @Autowired
    public TransferController(TransferSagaService transferSagaService) {
        this.transferSagaService = transferSagaService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<String> initiateTransfer(@RequestParam String accountFromId,
                                                   @RequestParam String accountToId,
                                                   @RequestParam BigDecimal amount) {
        try {
            transferSagaService.initiateTransferSaga(accountFromId, accountToId, amount);
            return ResponseEntity.ok("Transfer initiated successfully");
        } catch (InsufficientFundsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Transfer failed: " + e.getMessage());
        }
    }
}
