package com.mindhub.homebanking1.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mindhub.homebanking1.dtos.LoanApplicationDTO;
import com.mindhub.homebanking1.dtos.LoanDTO;
import com.mindhub.homebanking1.models.*;
import com.mindhub.homebanking1.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
public class LoanController {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private ClientLoanRepository clientLoanRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @RequestMapping(value = "/loans")
    @JsonIgnore
    public List<LoanDTO> getLoans(){
        return loanRepository.findAll().stream().map(LoanDTO::new).collect(Collectors.toList());
    }

    @RequestMapping(path = "/loans", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<Object> newLoan(@RequestBody LoanApplicationDTO loanApplicationDTO,
                                           Authentication authentication){

        Optional<Loan> loan = loanRepository.findById(loanApplicationDTO.getLoanId());
        Account accountDestino = accountRepository.findByNumber(loanApplicationDTO.getToAccountNumber());
        Client client = clientRepository.findByEmail(authentication.getName());

        if (loanApplicationDTO.getAmount() <= 0 || loanApplicationDTO.getPayments() <= 0
                || loanApplicationDTO.getLoanId() <= 0) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        if (loanApplicationDTO.getToAccountNumber().isEmpty()) {
            return new ResponseEntity<>("Missing data", HttpStatus.FORBIDDEN);
        }
        //Verificar que el préstamo exista
        if(!loanRepository.existsById(loanApplicationDTO.getLoanId())){
            return new ResponseEntity<>("The Loan doesn't exist", HttpStatus.FORBIDDEN);
        }
        //Verificar que el monto solicitado no exceda el monto máximo del préstamo
        if(loanApplicationDTO.getAmount() > loan.get().getMaxAmount()){
            return new ResponseEntity<>("The amount exceds the amount alowed", HttpStatus.FORBIDDEN);
        }
        //Verifica que la cantidad de cuotas se encuentre entre las disponibles del préstamo
        if(!loan.get().getPayments().contains(loanApplicationDTO.getPayments())){
            return new ResponseEntity<>("This payment doesn't exist", HttpStatus.FORBIDDEN);
        }
        //Verificar que la cuenta de destino exista
        if(accountDestino ==  null){
            return new ResponseEntity<>("The Account doesn't exist", HttpStatus.FORBIDDEN);
        }

        if(client != accountDestino.getClient()){
            return new ResponseEntity<>("The Account doesn't belong to the client", HttpStatus.FORBIDDEN);
        }

        double amount1 = loanApplicationDTO.getAmount() + ((loanApplicationDTO.getAmount())*0.20);

        clientLoanRepository.save(new ClientLoan(amount1, loanApplicationDTO.getPayments(), client, loan.get()));
        transactionRepository.save(new Transaction(TransactionType.CREDIT, loanApplicationDTO.getAmount(), loan.get().getName() + " loan approved",
                LocalDateTime.now(), accountDestino));//Credit

        accountDestino.setBalance(accountDestino.getBalance()+loanApplicationDTO.getAmount());

        accountRepository.save(accountDestino);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
