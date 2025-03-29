package br.com.hyperativa.card.model.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor
public class ApiResponse implements Serializable{
	private static final long serialVersionUID = 2657821711363752556L;
	
	private Boolean success;
    private String message;
}
