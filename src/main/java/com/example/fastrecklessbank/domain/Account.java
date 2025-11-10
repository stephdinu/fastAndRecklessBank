package com.example.fastrecklessbank.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class Account {
    private Long id;

    private String accountNumber;

    private AccountType accountType;

    private String accountHolderName;

    private String accountHolderContact;

    private BigDecimal balance;

    private boolean isClosed;

    public Account(AccountBuilder builder) {
        this.id = builder.id;
        this.accountNumber = builder.accountNumber;
        this.accountType = builder.accountType;
        this.balance = builder.balance;
        this.accountHolderName = builder.accountHolderName;
        this.accountHolderContact = builder.accountHolderContact;
        this.isClosed = builder.isClosed;
    }

    // Getters and Setters
    public Long getId() { return id; }

    public String getAccountNumber() { return accountNumber; }

    public AccountType getAccountType() { return accountType; }

    public String getAccountHolderName() { return accountHolderName; }

    public String getAccountHolderContact() { return accountHolderContact; }

    public BigDecimal getBalance() { return balance; }

    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public boolean isClosed() { return isClosed; }

    public static class AccountBuilder {

        private Long id;
        private final String accountNumber;
        private final AccountType accountType;
        private BigDecimal balance = BigDecimal.ZERO;
        private final String accountHolderName;
        private final String accountHolderContact;
        private boolean isClosed = false;   //default value.

        public AccountBuilder(AccountType accountType, String accountHolderName,
                              String accountHolderContact) {
            this.id = generateRandomId();
            this.accountNumber = "FRB-" + String.format("%05d", id);
            this.accountType = Objects.requireNonNull(accountType, "Account type must not be null");
            this.accountHolderName = Objects.requireNonNull(accountHolderName, "Account holder name must not be null");
            this.accountHolderContact = Objects.requireNonNull(accountHolderContact, "Account holder contact must not be null");
        }

        public AccountBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AccountBuilder balance(BigDecimal balance) {
            this.balance = balance;
            return this;
        }

        public AccountBuilder isClosed(boolean isClosed) {
            this.isClosed = isClosed;
            return this;
        }

        public Account build() {
            return new Account(this);
        }

        private Long generateRandomId() {
            return Instant.now().toEpochMilli();
        }
    }
}
