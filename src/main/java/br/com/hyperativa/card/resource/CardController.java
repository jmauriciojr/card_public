package br.com.hyperativa.card.resource;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import br.com.hyperativa.card.exception.ErrorResponse;
import br.com.hyperativa.card.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/card")
@RequiredArgsConstructor
public class CardController {

	private final CardService cardService;

	@Operation(summary = "Add card(s)")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Card(s) successfuly saved."),
			@ApiResponse(responseCode = "400", description = "Invalid request!", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(responseCode = "401", description = "Not Authorized!", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }),
			@ApiResponse(responseCode = "500", description = "Unexpected Error!", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
	@PostMapping
	public ResponseEntity<?> addCard(
			@Parameter(description = "Card number for register", required = false)
			@RequestParam(value = "cardNumber", required = false) String cardNumber,
			@Parameter(description = "File for register cards in lot", required = false)
			@RequestParam(value = "file", required = false) MultipartFile file) throws Exception {
		
        log.info("method=addCard, step=starting, cardNumber={}", cardNumber);
        
        String result = cardService.processAddCard(cardNumber, file);

        log.info("method=addCard, step=finishing, result={}", result);
		return ResponseEntity.ok(result);
	}
	
	
	@Operation(summary = "Get card by number")
	@ApiResponses(value = { 
			@ApiResponse(responseCode = "200", description = "Card retrieved successfully."),
			@ApiResponse(responseCode = "500", description = "Unexpected Error!", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) }) })
	@GetMapping(value = "/{cardNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Long> getCard(
			@Parameter(description = "Card Number [*Required]", required = true) 
			@PathVariable("cardNumber") @NotEmpty String cardNumber) {
		
        log.info("method=getCard, step=starting, cardNumber={}", cardNumber);

		return ResponseEntity.ok(cardService.findCard(cardNumber));
	}

}
