package br.com.hyperativa.card.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import br.com.hyperativa.card.model.dto.CardRecordDTO;
import br.com.hyperativa.card.model.dto.FileDataDTO;
import br.com.hyperativa.card.model.dto.FileHeaderDTO;
import br.com.hyperativa.card.model.dto.FileTrailerDTO;

public class FileParser {

    public static FileDataDTO parseFile(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        FileHeaderDTO header = null;
        List<CardRecordDTO> records = new ArrayList<>();
        FileTrailerDTO trailer = null;

        String line;
        boolean headerParsed = false;
        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                continue;
            }
            
            if (!headerParsed && !line.startsWith("C") && !line.startsWith("LOTE")) {
                header = parseHeader(line);
                headerParsed = true;
            } else if (line.startsWith("C")) {
                CardRecordDTO record = parseRecord(line);
                records.add(record);
            } else if (line.startsWith("LOTE")) {
                trailer = parseTrailer(line);
            }
        }
        return new FileDataDTO(header, records, trailer);
    }

    // Para o header: 
    // [01-29] NOME, [30-37] DATA, [38-45] LOTE, [46-51] QTD DE REGISTROS
    private static FileHeaderDTO parseHeader(String line) {
        String name = line.length() >= 29 ? line.substring(0, 29).trim() : "";
        String date = line.length() >= 37 ? line.substring(29, 37).trim() : "";
        String lotName = line.length() >= 45 ? line.substring(37, 45).trim() : "";
        String records = line.length() >= 51 ? line.substring(45, 51).trim() : "";
        
        return new FileHeaderDTO(name, date, lotName, records);
    }

    // Para os registros (linhas iniciadas com "C"):
    // [01] IDENTIFICADOR DA LINHA, [02-07] NUMERAÇÃO NO LOTE, [08-26] NÚMERO DE CARTAO COMPLETO
    private static CardRecordDTO parseRecord(String line) {
    	
        String lineIdentification = line.length() >= 1 ? line.substring(0, 1).trim() : "";
        String lineNumber = line.length() >= 7 ? line.substring(1, 7).trim() : "";
        String cardNumber = line.length() >= 26 ? line.substring(7, 26).trim() : "";
        
        return new CardRecordDTO(lineIdentification, lineNumber, cardNumber);
    }

    // Para o trailer: 
    // [01-08] LOTE, [09-14] QTD DE REGISTROS
    private static FileTrailerDTO parseTrailer(String line) {
        String lotName = line.length() >= 8 ? line.substring(0, 8).trim() : "";
        String records = line.length() >= 14 ? line.substring(8, 14).trim() : "";
        
        return new FileTrailerDTO(lotName, Integer.parseInt(records));
    }
}