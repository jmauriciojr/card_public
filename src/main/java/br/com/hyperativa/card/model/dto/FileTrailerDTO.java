package br.com.hyperativa.card.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileTrailerDTO {

	private String lotName;
    private Integer records;
}
