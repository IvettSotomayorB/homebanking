package com.mindhub.homebanking1.repositories;

import com.mindhub.homebanking1.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
