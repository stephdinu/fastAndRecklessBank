package com.example.fastrecklessbank.dto;

import com.example.fastrecklessbank.domain.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class TransactionDTO {

    private final String accountNumber;
    private final TransactionType transactionType;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;

    public TransactionDTO(String accountNumber, TransactionType transactionType, BigDecimal amount, LocalDateTime timestamp) {
        this.accountNumber = accountNumber;
        this.transactionType = transactionType;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
