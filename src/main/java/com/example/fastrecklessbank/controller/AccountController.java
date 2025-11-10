package com.example.fastrecklessbank.controller;

import com.example.fastrecklessbank.domain.AccountRequest;
import com.example.fastrecklessbank.dto.AccountDTO;
import com.example.fastrecklessbank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@RequestMapping(path = "/api/accounts")
public class AccountController {

    @Autowired
    public AccountService accountService;

    @GetMapping("/allAccounts")
    public ResponseEntity<List<AccountDTO>> listAllAccounts() {
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @PostMapping("/createNewAccount")
    public ResponseEntity<AccountDTO> createAccount(@RequestBody AccountRequest accountRequest) {
        AccountDTO createdAccount = accountService.createAccount(accountRequest.getType(),
                accountRequest.getHolder(), accountRequest.getContact());

        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }

    @PostMapping("/updateAccount")
    public ResponseEntity<AccountDTO> updateAccount(@RequestParam String accountNumber, @RequestBody AccountDTO accountDTO)
            throws AccountNotFoundException {
        try {
            AccountDTO updatedAccount = accountService.updateAccount(accountNumber, accountDTO);
            return new ResponseEntity<>(updatedAccount, HttpStatus.OK);
        } catch (AccountNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/closeAccount")
    public ResponseEntity<AccountDTO> closeAccount(@RequestParam String accountNumber) throws AccountNotFoundException {
        try {
            AccountDTO closeAccount = accountService.closeAccount(accountNumber);
            return new ResponseEntity<>(closeAccount,HttpStatus.OK);
        } catch (AccountNotFoundException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }
}
