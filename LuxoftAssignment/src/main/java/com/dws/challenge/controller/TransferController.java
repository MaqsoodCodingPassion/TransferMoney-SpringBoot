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

/**
 * TransferController class for managing money transfer operations.
 * This controller provides endpoints for initiating money transfers between accounts.
 */
@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferSagaService transferSagaService;

    /**
     * Constructs a new TransferController with the specified TransferSagaService.
     * @param transferSagaService The TransferSagaService responsible for handling money transfer operations.
     */
    @Autowired
    public TransferController(TransferSagaService transferSagaService) {
        this.transferSagaService = transferSagaService;
    }

    /**
     * Initiates a money transfer from one account to another.
     * @param accountFromId The ID of the account from which the transfer is initiated.
     * @param accountToId The ID of the account to which the transfer is made.
     * @param amount The amount of money to transfer.
     * @return ResponseEntity with a success message if the transfer is initiated successfully,
     *         or an error message if the transfer fails due to insufficient funds or other reasons.
     */
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
