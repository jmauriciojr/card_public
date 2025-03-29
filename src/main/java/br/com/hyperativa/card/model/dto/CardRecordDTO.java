package br.com.hyperativa.card.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CardRecordDTO {
	
	private String lineIdentification;
	private String lineNumber;
	private String cardNumber;

}
