package com.mindhub.homebanking1.dtos;

import com.mindhub.homebanking1.models.Account;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class AccountDTO {
    private long id;
    private String number;
    private LocalDateTime creationDate;
    private double balance;
    Set<TransactionDTO> transactions;

    public AccountDTO(Account account) {
        this.id = account.getId();
        this.number = account.getNumber();
        this.creationDate = account.getCreationDate();
        this.balance = account.getBalance();
        this.transactions = account.getTransactions().stream().map(TransactionDTO::new).collect(Collectors.toSet());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Set<TransactionDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<TransactionDTO> transactions) {
        this.transactions = transactions;
    }

/*  public Set<TransactionDTO> setTransactionsToSetTransactionsDTO(Set<Transaction> transactions) {
        for(Transaction transaction : transactions){
            transactiondto.add(new TransactionDTO(transaction));
        }
        return transactiondto;
    }

    public Set<TransactionDTO> getTransactions(){
        return setTransactionsToSetTransactionsDTO(this.transactions);
    }

    public void setTransactions(Set<TransactionDTO> transactiondto) {
        this.transactiondto = transactiondto;
    }*/
}
