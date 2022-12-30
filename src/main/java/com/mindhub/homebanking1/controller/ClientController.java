package com.mindhub.homebanking1.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mindhub.homebanking1.dtos.ClientDTO;
import com.mindhub.homebanking1.models.Account;
import com.mindhub.homebanking1.models.Client;
import com.mindhub.homebanking1.repositories.AccountRepository;
import com.mindhub.homebanking1.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
public class ClientController {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping(value = "/clients")
    @JsonIgnore
    public List<ClientDTO> getClients(){
        //return clientRepository.findAll().stream().map(client -> new ClientDTO(client)).collect(toList());
        return clientRepository.findAll().stream().map(ClientDTO::new).collect(Collectors.toList());
    }
    @RequestMapping(value = "/clients/{id}")
    public ClientDTO getClient(@PathVariable Long id){
        return clientRepository.findById(id).map(ClientDTO::new).orElse(null);
    }
    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @RequestMapping(path = "/clients", method = RequestMethod.POST)
    public ResponseEntity<Object> register(@RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }

        if (clientRepository.findByEmail(email) !=  null) {
            return new ResponseEntity<>("Name already in use", HttpStatus.FORBIDDEN);
        }

        Client client = clientRepository.save(new Client(firstName, lastName, email, passwordEncoder.encode(password)));
        accountRepository.save(new Account("VIN" + String.format("%08d", getRandomNumber(0, 99999999)), LocalDateTime.now(), 0, client));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @RequestMapping(value = "/clients/current")
    public ClientDTO getAll(Authentication authentication) {
        Client client = clientRepository.findByEmail(authentication.getName());
        return new ClientDTO(client);
    }
}
