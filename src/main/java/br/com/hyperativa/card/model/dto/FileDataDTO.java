package br.com.hyperativa.card.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDataDTO {

	private FileHeaderDTO header;
    private List<CardRecordDTO> records;
    private FileTrailerDTO trailer;
}
