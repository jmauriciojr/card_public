package br.com.hyperativa.card.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private String timestamp;
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now().format(formatter);
    }
}