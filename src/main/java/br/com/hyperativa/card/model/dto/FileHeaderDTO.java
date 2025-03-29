package br.com.hyperativa.card.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileHeaderDTO {
	
    private String name;
    private String date;
    private String lotName;
    private String records;

}
