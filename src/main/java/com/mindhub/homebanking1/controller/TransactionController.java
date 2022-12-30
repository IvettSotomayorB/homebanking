package com.mindhub.homebanking1.controller;

import com.mindhub.homebanking1.models.*;
import com.mindhub.homebanking1.repositories.AccountRepository;
import com.mindhub.homebanking1.repositories.ClientRepository;
import com.mindhub.homebanking1.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/api")
public class TransactionController {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    
    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Object> transfer(@RequestParam String fromAccountNumber, @RequestParam String toAccountNumber,
                                           @RequestParam double amount, @RequestParam String description,
                                           Authentication authentication){

        Account accountOrigen = accountRepository.findByNumber(fromAccountNumber);
        Account accountDestino = accountRepository.findByNumber(toAccountNumber);
        Client client = clientRepository.findByEmail(authentication.getName());

        if(amount <= 0){
            return new ResponseEntity<>("The amount can't be 0", HttpStatus.FORBIDDEN);
        }

        if (description.isEmpty() || fromAccountNumber.isEmpty() || toAccountNumber.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if(fromAccountNumber.contentEquals(toAccountNumber)){
            return new ResponseEntity<>("The Accounts are the same", HttpStatus.FORBIDDEN);
        }

        if(accountRepository.findByNumber(fromAccountNumber) ==  null){
            return new ResponseEntity<>("The Account doesn't exist", HttpStatus.FORBIDDEN);
        }

        if(client != accountOrigen.getClient()){
            return new ResponseEntity<>("The Account doesn't belong to the client", HttpStatus.FORBIDDEN);
        }

        if(accountRepository.findByNumber(toAccountNumber) ==  null){
            return new ResponseEntity<>("The Account doesn't exist", HttpStatus.FORBIDDEN);
        }

        if(accountOrigen.getBalance() < amount){
            return new ResponseEntity<>("You dont have enough money", HttpStatus.FORBIDDEN);
        }

        transactionRepository.save(new Transaction(TransactionType.DEBIT, -amount, description + " " + accountDestino.getNumber(),
                LocalDateTime.now(), accountOrigen));//Debit
        transactionRepository.save(new Transaction(TransactionType.CREDIT, amount, description + " " + accountOrigen.getNumber(),
                LocalDateTime.now(), accountDestino));//Credit

        accountOrigen.setBalance(accountOrigen.getBalance()-amount);
        accountDestino.setBalance(accountDestino.getBalance()+amount);

        accountRepository.save(accountOrigen);
        accountRepository.save(accountDestino);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
