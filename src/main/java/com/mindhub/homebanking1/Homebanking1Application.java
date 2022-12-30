package com.mindhub.homebanking1;

import com.mindhub.homebanking1.models.*;
import com.mindhub.homebanking1.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootApplication
public class Homebanking1Application {
	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(Homebanking1Application.class, args);
	}
	@Bean
	public CommandLineRunner initData(ClientRepository repository, AccountRepository accountRepository,
									  TransactionRepository transactionRepository, LoanRepository loanRepository,
									  ClientLoanRepository clientLoanRepository, CardRepository cardRepository) {
		return (args) -> {
			Client client1 = repository.save(new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("abc1234")));
			Client client2 = repository.save(new Client("Maria", "Perez", "mariaPerez@mindhub.com", passwordEncoder.encode("Def5678")));
			Client client3 = repository.save(new Client("Franco", "Soto", "francoSoto@mindhub.com", passwordEncoder.encode("Ghi9012")));

			Account account1 = accountRepository.save(new Account("VIN001", LocalDateTime.now(), 5000, client1));
			Account account2 = accountRepository.save(new Account("VIN002", LocalDateTime.now().plusDays(1), 7500, client1));
			Account account3 = accountRepository.save(new Account("VIN003", LocalDateTime.now().minusDays(1), 8200, client2));
			Account account4 = accountRepository.save(new Account("VIN004", LocalDateTime.now(), 9850, client3));

			transactionRepository.save(new Transaction(TransactionType.CREDIT, 3000,
					"Transaccion con Credito 1 de cuenta VIN001", LocalDateTime.now(), account1));
			transactionRepository.save(new Transaction(TransactionType.DEBIT, -1000,
					"Transaccion con Debito 2 de cuenta VIN001", LocalDateTime.now(), account1));
			transactionRepository.save(new Transaction(TransactionType.DEBIT, -2000,
					"Transaccion con Debito 2 de cuenta VIN002", LocalDateTime.now(), account2));
			transactionRepository.save(new Transaction(TransactionType.CREDIT, 1000,
					"Transaccion con Credito 1 de cuenta VIN003", LocalDateTime.now(), account3));
			transactionRepository.save(new Transaction(TransactionType.CREDIT, 4000,
					"Transaccion con Credito 1 de cuenta VIN004", LocalDateTime.now(), account4));

			Loan loan1 = loanRepository.save(new Loan("Hipotecario", 500000, List.of(12, 24, 36, 48, 60)));
			Loan loan2 = loanRepository.save(new Loan("Personal", 100000, List.of(6, 12, 24)));
			Loan loan3 = loanRepository.save(new Loan("Automotriz", 300000, List.of(6, 12, 24, 36)));

			ClientLoan clientLoan = new ClientLoan(400000, 60, client1, loan1);
			clientLoanRepository.save(clientLoan);
			ClientLoan clientLoan1 = new ClientLoan(50000, 12, client1, loan2);
			clientLoanRepository.save(clientLoan1);

			ClientLoan clientLoan2 = new ClientLoan(100000, 24, client2, loan2);
			clientLoanRepository.save(clientLoan2);
			ClientLoan clientLoan3 = new ClientLoan(200000, 36, client2, loan3);
			clientLoanRepository.save(clientLoan3);

			ClientLoan clientLoan4 = new ClientLoan(500000, 48, client3, loan1);
			clientLoanRepository.save(clientLoan4);
			ClientLoan clientLoan5 = new ClientLoan(250000, 36, client3, loan3);
			clientLoanRepository.save(clientLoan5);

			Card card1 = new Card(client1.getFirstName() + " " + client1.getLastName(), CardType.DEBIT,
					CardColor.GOLD, "4123 7532 0001 4890","364", LocalDateTime.now(),
					LocalDateTime.now().plusYears(5), client1);
			cardRepository.save(card1);

			Card card2 = new Card(client1.getFirstName() + " " + client1.getLastName(), CardType.CREDIT,
					CardColor.TITANIUM, "5923 6110 8080 0279","985", LocalDateTime.now(),
					LocalDateTime.now().plusYears(5), client1);
			cardRepository.save(card2);

			Card card3 = new Card(client2.getFirstName() + " " + client2.getLastName(), CardType.CREDIT,
					CardColor.SILVER, "5911 6300 9364 0034","701", LocalDateTime.now().plusDays(2),
					LocalDateTime.now().plusDays(2).plusYears(5), client2);
			cardRepository.save(card3);

			Card card4 = new Card(client3.getFirstName() + " " + client3.getLastName(), CardType.DEBIT,
					CardColor.TITANIUM, "4908 3711 0004 9845","566", LocalDateTime.now().plusDays(5),
					LocalDateTime.now().plusDays(5).plusYears(5), client3);
			cardRepository.save(card4);
		};
	}
}
