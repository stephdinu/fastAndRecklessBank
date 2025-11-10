package com.example.fastrecklessbank.service;

import com.example.fastrecklessbank.domain.Account;
import com.example.fastrecklessbank.domain.AccountType;
import com.example.fastrecklessbank.dto.AccountDTO;
import com.example.fastrecklessbank.mapper.AccountMapper;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Service
public class AccountService {

    private Map<String, AccountDTO> accountRepository =  new HashMap<>();

    private final AccountMapper accountMapper = new AccountMapper();

    public List<AccountDTO> getAllAccounts() {
        return accountRepository.values().stream().toList();
    }

    public AccountDTO getAccountByNumber(String accountNumber) throws AccountNotFoundException {
        return Optional.ofNullable(accountRepository.get(accountNumber))
                .orElseThrow(() -> new AccountNotFoundException("Account with number " + accountNumber + " not found"));
    }

    public AccountDTO createAccount(AccountType accountType, String accountHolderName, String accountHolderContact) {
        Account account = new Account.AccountBuilder(accountType, accountHolderName, accountHolderContact).build();

        AccountDTO accountDTO = accountMapper.toDTO(account);

        accountRepository.put(account.getAccountNumber(), accountDTO);

        return accountDTO;
    }

    public AccountDTO updateAccount(String accountNumber, AccountDTO updatedAccountDTO) throws AccountNotFoundException {
        /*AccountDTO existingAccountDTO = Optional.ofNullable(accountRepository.get(accountNumber))
                .orElseThrow(() -> new AccountNotFoundException("Account with number " + accountNumber + " not found"));

        Account existingAccount = accountMapper.toDomain(existingAccountDTO);

        if (existingAccount.isClosed()) {
            throw new IllegalStateException("Account is closed and cannot be updated");
        }

        Account toBeUpdatedAccount = accountMapper.toDomain(updatedAccountDTO);

        Account updatedAccount = new Account.AccountBuilder(
                existingAccount.getAccountType(),
                toBeUpdatedAccount.getAccountHolderName(),
                toBeUpdatedAccount.getAccountHolderContact()
        ).id(existingAccount.getId())
                .balance(toBeUpdatedAccount.getBalance())
                .isClosed(existingAccount.isClosed())
                .build();*/

        //AccountDTO savedAccountDTO = accountMapper.toDTO(updatedAccount);
        accountRepository.put(accountNumber, updatedAccountDTO);
        return updatedAccountDTO;

    }

    public AccountDTO closeAccount(String accountNumber) throws AccountNotFoundException {
        AccountDTO existingAccountDTO = Optional.ofNullable(accountRepository.get(accountNumber))
                .orElseThrow(() -> new AccountNotFoundException("Account with number " + accountNumber + " not found"));

        if (existingAccountDTO.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Account cannot be closed because it has a non-zero balance.");
        }

        Account toBeClosed = new Account.AccountBuilder(
                existingAccountDTO.getAccountType(),
                existingAccountDTO.getAccountHolderName(),
                existingAccountDTO.getAccountHolderContact()
        ).balance(existingAccountDTO.getBalance())
                .id(accountMapper.toDomain(existingAccountDTO).getId())
                .isClosed(true)
                .build();

        AccountDTO closedDTO = accountMapper.toDTO(toBeClosed);

        accountRepository.put(existingAccountDTO.getAccountNumber(), closedDTO);

        return closedDTO;
    }

    public BigDecimal getBalance(String accountNumber) throws AccountNotFoundException {
        AccountDTO existingAccountDTO = Optional.ofNullable(accountRepository.get(accountNumber))
                .orElseThrow(() -> new AccountNotFoundException("Account with number " + accountNumber + " not found"));

        return existingAccountDTO.getBalance();
    }

    private String generateRandomAccountNumber() {
        long timestamp = Instant.now().toEpochMilli();
        int randomNumber = new Random().nextInt(10000);
        return String.format("%d-%04d", timestamp, randomNumber);
    }
}
