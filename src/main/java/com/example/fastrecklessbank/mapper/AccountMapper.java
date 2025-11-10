package com.example.fastrecklessbank.mapper;

import com.example.fastrecklessbank.domain.Account;
import com.example.fastrecklessbank.dto.AccountDTO;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toDomain(AccountDTO accountDTO) {
        if (accountDTO == null) {
            return null;
        }
        return new Account.AccountBuilder (
                accountDTO.getAccountType(),
                accountDTO.getAccountHolderName(),
                accountDTO.getAccountHolderContact()
        )
                .balance(accountDTO.getBalance())
                .isClosed(accountDTO.isClosed())
                .build();
    }

    public AccountDTO toDTO(Account account) {
        if (account == null) {
            return null;
        }
        return new AccountDTO(
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getAccountHolderName(),
                account.getAccountHolderContact(),
                account.isClosed()
        );
    }
}
