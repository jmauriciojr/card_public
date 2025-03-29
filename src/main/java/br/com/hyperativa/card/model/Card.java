package br.com.hyperativa.card.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "card", uniqueConstraints = {
	    @UniqueConstraint(columnNames = "cardHash")
	})
public class Card {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cardHash;
    
    public Card(String cardHash) {
        this.cardHash = cardHash;
    }
}
