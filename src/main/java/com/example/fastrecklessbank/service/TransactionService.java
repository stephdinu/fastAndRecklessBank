package com.example.fastrecklessbank.service;

import com.example.fastrecklessbank.domain.Account;
import com.example.fastrecklessbank.domain.Transaction;
import com.example.fastrecklessbank.dto.AccountDTO;
import com.example.fastrecklessbank.dto.TransactionDTO;
import com.example.fastrecklessbank.exception.InsufficientFundsException;
import com.example.fastrecklessbank.mapper.AccountMapper;
import com.example.fastrecklessbank.mapper.TransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static com.example.fastrecklessbank.domain.TransactionType.*;

@Service
public class TransactionService {

    @Autowired
    private AccountService accountService;
    private final AccountMapper accountMapper = new AccountMapper();
    private final TransactionMapper transactionMapper = new TransactionMapper();

    private Map<String, List<TransactionDTO>> transactionRepository = new HashMap<>();

    public List<TransactionDTO> listAllTransactions() {
        return transactionRepository.values().stream().flatMap(Collection::stream).toList();
    }

    public TransactionDTO deposit(String accountNumber, BigDecimal amount) throws AccountNotFoundException {
        AccountDTO accountDTO = accountService.getAccountByNumber(accountNumber);
        Account account = accountMapper.toDomain(accountDTO);
        account.setBalance(account.getBalance().add(amount));
        AccountDTO updatedBalanceDTO = accountMapper.toDTO(account);
        accountService.updateAccount(accountNumber, updatedBalanceDTO);

        Transaction deposit = new Transaction(accountNumber, DEPOSIT, amount, LocalDateTime.now());
        if (transactionRepository.get(accountNumber) == null || transactionRepository.get(accountNumber).isEmpty()) {
            transactionRepository.put(accountNumber, List.of(transactionMapper.toDTO(deposit)));
        } else {
            List<TransactionDTO> transactionBucket = transactionRepository.get(accountNumber);
            transactionBucket.add(transactionMapper.toDTO(deposit));
            transactionRepository.put(accountNumber, transactionBucket);
        }

        return transactionMapper.toDTO(deposit);
    }

    public TransactionDTO withdraw(String accountNumber, BigDecimal amount) throws AccountNotFoundException {
        AccountDTO accountDTO = accountService.getAccountByNumber(accountNumber);
        Account account = accountMapper.toDomain(accountDTO);
        if (account.getBalance().compareTo(amount) <= 0) {
            throw new InsufficientFundsException("Insufficient balance!");
        }
        account.setBalance(account.getBalance().subtract(amount));
        AccountDTO updatedBalanceDTO = accountMapper.toDTO(account);
        accountService.updateAccount(accountNumber, updatedBalanceDTO);

        Transaction withdrawal = new Transaction(accountNumber, WITHDRAWAL, amount, LocalDateTime.now());
        if (transactionRepository.get(accountNumber) == null) {
            TransactionDTO withdrawlDTO = transactionMapper.toDTO(withdrawal);
            transactionRepository.put(accountNumber, List.of(withdrawlDTO));
        } else {
            List<TransactionDTO> transactionBucket = new ArrayList<>();
            transactionBucket.addAll(transactionRepository.get(accountNumber));
            TransactionDTO withdrawlDTO = transactionMapper.toDTO(withdrawal);
            transactionBucket.add(withdrawlDTO);
            transactionRepository.put(accountNumber, transactionBucket);
        }

        return transactionMapper.toDTO(withdrawal);
    }

    public TransactionDTO transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) throws AccountNotFoundException {
        withdraw(fromAccountNumber, amount);
        deposit(toAccountNumber, amount);

        Transaction transfer = new Transaction(fromAccountNumber, TRANSFER, amount, LocalDateTime.now());
        /*if (transactionRepository.get(fromAccountNumber) == null || transactionRepository.get(fromAccountNumber).isEmpty()) {
            TransactionDTO transferDTO = transactionMapper.toDTO(transfer);
            transactionRepository.put(fromAccountNumber, List.of(transferDTO));
        } else {
            List<TransactionDTO> transactionBucket = new ArrayList<>();
            transactionBucket.addAll(transactionRepository.get(fromAccountNumber));
            TransactionDTO transferDTO = transactionMapper.toDTO(transfer);
            transactionBucket.add(transferDTO);
            transactionRepository.put(fromAccountNumber, transactionBucket);
        }*/

        return transactionMapper.toDTO(transfer);
    }

    public List<TransactionDTO> getTransactionHistory(String accountNumber) {

        return transactionRepository.get(accountNumber);
    }
}
