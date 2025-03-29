package br.com.hyperativa.card.exception;

public class CardNotFoundException extends RuntimeException {
	
    private static final long serialVersionUID = -2270408349299592723L;

	public CardNotFoundException(String message) {
        super(message);
    }
}