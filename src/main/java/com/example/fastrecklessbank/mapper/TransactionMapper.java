package com.example.fastrecklessbank.mapper;

import com.example.fastrecklessbank.domain.Transaction;
import com.example.fastrecklessbank.dto.TransactionDTO;
import org.springframework.stereotype.Component;

@Component
public class TransactionMapper {

    public Transaction toDomain(TransactionDTO transactionDTO) {
        if (transactionDTO == null) {
            return null;
        }
        return new Transaction(
                transactionDTO.getAccountNumber(),
                transactionDTO.getTransactionType(),
                transactionDTO.getAmount(),
                transactionDTO.getTimestamp()
        );
    }

    public TransactionDTO toDTO(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        return new TransactionDTO(
                transaction.getAccountNumber(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getTimestamp()
        );
    }
}

