package com.mindhub.homebanking1.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mindhub.homebanking1.dtos.AccountDTO;
import com.mindhub.homebanking1.models.Account;
import com.mindhub.homebanking1.models.Client;
import com.mindhub.homebanking1.repositories.AccountRepository;
import com.mindhub.homebanking1.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping(value = "/accounts")
    @JsonIgnore
    public List<AccountDTO> getAccounts(){
        return accountRepository.findAll().stream().map(AccountDTO::new).collect(Collectors.toList());
    }
    @RequestMapping(value = "/accounts/{id}")
    public AccountDTO getAccount(@PathVariable Long id){
        return accountRepository.findById(id).map(AccountDTO::new).orElse(null);
    }
    @GetMapping("/clients/current/accounts")
    public List<AccountDTO> getAccounts(Authentication authentication){
        Client client = this.clientRepository.findByEmail(authentication.getName());
        return client.getAccounts().stream().map(AccountDTO::new).collect(Collectors.toList());
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @RequestMapping(value = "/clients/current/accounts", method = RequestMethod.POST)
    public ResponseEntity<Object> newAccount(Authentication authentication){

        Client client = clientRepository.findByEmail(authentication.getName());

        if(client.getAccounts().size() == 3 ){
            return new ResponseEntity<>("Ya tienes 3 cuentas creadas", HttpStatus.FORBIDDEN);
        }

        accountRepository.save(new Account("VIN" + String.format("%08d", getRandomNumber(1, 99999999)), LocalDateTime.now(), 0, client));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}