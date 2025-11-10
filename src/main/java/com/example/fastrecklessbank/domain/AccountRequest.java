package com.example.fastrecklessbank.domain;

public class AccountRequest {
    private String holder;
    private String contact;
    private AccountType type;

    public AccountRequest(String accountHolderName, String accountHolderContact, AccountType accountType) {
        this.holder = accountHolderName;
        this.contact = accountHolderContact;
        this.type = accountType;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }
}
