package br.com.hyperativa.card.service;

import org.springframework.web.multipart.MultipartFile;

public interface CardService {

	
	String processAddCard(String cardNumber, MultipartFile file);
    Long findCard(String cardNumber);

}
