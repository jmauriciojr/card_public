package br.com.hyperativa.card.exception;

public class CardProcessingException extends RuntimeException {
	
    private static final long serialVersionUID = -2270408349299592723L;

	public CardProcessingException(String message) {
        super(message);
    }
}