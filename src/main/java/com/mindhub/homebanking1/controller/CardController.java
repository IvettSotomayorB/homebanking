package com.mindhub.homebanking1.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mindhub.homebanking1.dtos.CardDTO;
import com.mindhub.homebanking1.models.Card;
import com.mindhub.homebanking1.models.CardColor;
import com.mindhub.homebanking1.models.CardType;
import com.mindhub.homebanking1.models.Client;
import com.mindhub.homebanking1.repositories.CardRepository;
import com.mindhub.homebanking1.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
public class CardController {
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping(value = "/cards")
    @JsonIgnore
    public List<CardDTO> getCards(){
        return cardRepository.findAll().stream().map(CardDTO::new).collect(Collectors.toList());
    }
    @RequestMapping(value = "/cards/{id}")
    public CardDTO getCard(@PathVariable Long id){
        return cardRepository.findById(id).map(CardDTO::new).orElse(null);
    }

    private int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    @RequestMapping(value = "/clients/current/cards", method = RequestMethod.POST)
    public ResponseEntity<Object> newCard(@RequestParam CardType cardType, @RequestParam CardColor cardColor,
                                          Authentication authentication){

        String cardNumber = String.format("%04d %04d %04d %04d", getRandomNumber(1, 9999), getRandomNumber(1, 9999),
                getRandomNumber(1, 9999), getRandomNumber(1, 9999));
        String cvv = String.format("%03d",getRandomNumber(1, 999));

        Client client = clientRepository.findByEmail(authentication.getName());

        Set<Card> cards = client.getCards();
        Set<Card> creditCards = new HashSet<>();
        Set<Card> debitCards  = new HashSet<>();

        for(Card card : cards){
            if(card.getType() == CardType.DEBIT){
                debitCards.add(card);
            } else if(card.getType() == CardType.CREDIT){
                creditCards.add(card);
            }
        }

        if(cardType == CardType.DEBIT && debitCards.size() == 3){
            return new ResponseEntity<>("Ya tienes 3 tarjetas creadas", HttpStatus.FORBIDDEN);
        }

        if(cardType == CardType.CREDIT && creditCards.size() == 3){
            return new ResponseEntity<>("Ya tienes 3 tarjetas creadas", HttpStatus.FORBIDDEN);
        }

        cardRepository.save(new Card(client.getFirstName() + " " + client.getLastName(), cardType, cardColor,
                cardNumber, cvv , LocalDateTime.now(),
                LocalDateTime.now().plusYears(5), client));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
