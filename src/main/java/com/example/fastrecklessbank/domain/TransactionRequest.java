package com.example.fastrecklessbank.domain;

import java.math.BigDecimal;

public class TransactionRequest {
    private BigDecimal amount;
    private String accountNumber;

    public TransactionRequest(BigDecimal amount, String accountNumber) {
        this.amount = amount;
        this.accountNumber = accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String toAccountNumber) {
        this.accountNumber = toAccountNumber;
    }
}

