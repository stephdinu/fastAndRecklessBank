package com.example.fastrecklessbank.domain;

import java.math.BigDecimal;

public class TransferRequest {
    private BigDecimal amount;
    private String toAccountNumber;
    private String fromAccountNumber;

    public TransferRequest(BigDecimal amount, String toAccountNumber, String fromAccountNumber) {
        this.amount = amount;
        this.toAccountNumber = toAccountNumber;
        this.fromAccountNumber = fromAccountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    public String getFromAccountNumber() {
        return fromAccountNumber;
    }

    public void setFromAccountNumber(String fromAccountNumber) {
        this.fromAccountNumber = fromAccountNumber;
    }
}
