package com.example.fastrecklessbank.controller;

import com.example.fastrecklessbank.domain.TransactionRequest;
import com.example.fastrecklessbank.domain.TransferRequest;
import com.example.fastrecklessbank.dto.TransactionDTO;
import com.example.fastrecklessbank.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@RequestMapping("api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // to be implemented in the frontend
    @GetMapping("/allTransactions")
    public ResponseEntity<List<TransactionDTO>> listAllTransactions() {
        return ResponseEntity.ok(transactionService.listAllTransactions());
    }

    @GetMapping("/transactionHistory")
    public ResponseEntity<List<TransactionDTO>> getTransactionHistory(@RequestParam String accountNumber) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(accountNumber));
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionDTO> deposit(@RequestBody TransactionRequest transactionRequest) throws AccountNotFoundException {
        return ResponseEntity.ok(transactionService.deposit(transactionRequest.getAccountNumber(), transactionRequest.getAmount()));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionDTO> withdraw(@RequestBody TransactionRequest transactionRequest) throws AccountNotFoundException {
        return ResponseEntity.ok(transactionService.withdraw(transactionRequest.getAccountNumber(), transactionRequest.getAmount()));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionDTO> transfer(@RequestBody TransferRequest transferRequest) throws AccountNotFoundException {
        return ResponseEntity.ok(transactionService.transfer(transferRequest.getFromAccountNumber(), transferRequest.getToAccountNumber(), transferRequest.getAmount()));
    }
}
