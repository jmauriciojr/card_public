package br.com.hyperativa.card.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.hyperativa.card.model.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    Optional<Card> findByCardHash(String cardHash);

}
