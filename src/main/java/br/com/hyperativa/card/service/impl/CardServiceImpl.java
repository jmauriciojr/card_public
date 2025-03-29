package br.com.hyperativa.card.service.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.hyperativa.card.commons.Constants;
import br.com.hyperativa.card.exception.CardNotFoundException;
import br.com.hyperativa.card.exception.CardProcessingException;
import br.com.hyperativa.card.model.Card;
import br.com.hyperativa.card.model.dto.FileDataDTO;
import br.com.hyperativa.card.repository.CardRepository;
import br.com.hyperativa.card.service.CardService;
import br.com.hyperativa.card.util.FileParser;
import ch.qos.logback.core.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

	private final CardRepository cardRepository;

	@Override
	public Long findCard(String cardNumber) {
		log.info("method=findCard, step=starting, cardNumber={}", cardNumber);

		String hash = hashCard(cardNumber);
		return cardRepository.findByCardHash(hash)
				.orElseThrow(() -> new CardNotFoundException(String.format(Constants.CARD_NOT_FOUND, cardNumber)))
				.getId();
	}
	
	@Override
	public String processAddCard(String cardNumber, MultipartFile file) {
		log.info("method=addCard, step=starting, cardNumber={}, file={}", cardNumber, file);
		validateProcessAddCardRequest(cardNumber, file);

		if (!StringUtil.isNullOrEmpty(cardNumber)) {
			Long id = addCard(cardNumber);
			log.info("method=addCard, step=finishing, cardId={}", id);
			return "Card inserted/updated with ID: " + id;
		} else if (file != null) {
			String result = processCardFile(file);
			log.info("method=addCard, step=finishing, result={}", "\n" + result);
			return result;
		}
		return "";
	}


	private Long addCard(String cardNumber) {
		log.info("method=addCard, step=starting, cardNumber={}", cardNumber);

		String hash = hashCard(cardNumber);

		return cardRepository.findByCardHash(hash).map(Card::getId).orElseGet(() -> {
			Card card = new Card(hash);
			cardRepository.save(card);
			return card.getId();
		});
	}


	private String processCardFile(MultipartFile file) {
		log.info("method=processCardFile, step=starting");

		try {
			FileDataDTO fileData = FileParser.parseFile(file.getInputStream());
			StringBuilder responseBuilder = new StringBuilder();

			fileData.getRecords().forEach(record -> {
				String cardNumber = record.getCardNumber();
				if (cardNumber != null && !cardNumber.isBlank()) {
					Long id = addCard(cardNumber);
					
					log.info("method=processCardFile, step=cardInserted, cardNumber={}, id={}", cardNumber, id);
					responseBuilder.append("Card ")
					               .append(cardNumber)
					               .append(" inserted/updated with ID: ")
					               .append(id)
					               .append("\n");
				} else {
					responseBuilder.append("Card at line ")
		               .append(record.getLineIdentification())
		               .append(":")
		               .append(record.getLineNumber())
		               .append(" was skipped ")
		               .append("\n");
					log.warn("method=processCardFile, step=skipped, reason=cardNumber is null or blank");
				}
			});

			log.info("method=processCardFile, step=finishing");
			return responseBuilder.toString();
		} catch (IOException e) {
			log.error("method=processCardFile, step=error, message={}", e.getMessage());
			throw new CardProcessingException("Card processing error");
		}

	}


	private String hashCard(String cardNumber) {
		log.info("method=hashCard, step=starting, cardNumber={}", cardNumber);
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(cardNumber.getBytes(StandardCharsets.UTF_8));

			log.info("method=hashCard, step=finishing, cardNumber={}", cardNumber);
			return bytesToHex(encodedhash);
		} catch (NoSuchAlgorithmException e) {
			log.error("method=hashCard, step=error, message={}", e.getMessage());
			throw new CardProcessingException("Card processing error: " + cardNumber);
		}
	}

	private static String bytesToHex(byte[] hash) {
		log.info("method=bytesToHex, step=starting");

		StringBuilder hexString = new StringBuilder(2 * hash.length);
		for (byte b : hash) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}

		log.info("method=bytesToHex, step=finishing");
		return hexString.toString();
	}

	private void validateProcessAddCardRequest(String cardNumber, MultipartFile file) {

		if (StringUtil.isNullOrEmpty(cardNumber) && file == null) {
			throw new CardProcessingException("Enter the cardNumber or the TXT file.");
		}
	}

}
