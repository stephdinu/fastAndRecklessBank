package com.example.fastrecklessbank.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Random;

public class Transaction {
    private Long id;

    private String accountNumber;

    private TransactionType type;

    private BigDecimal amount;

    private LocalDateTime timestamp;

    public Transaction(String accountNumber, TransactionType type, BigDecimal amount, LocalDateTime timestamp) {
        this.id = generateRandomId();
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public TransactionType getTransactionType() {
        return type;
    }

    public void setTransactionType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    private Long generateRandomId() {
        return Instant.now().toEpochMilli();
    }
}
