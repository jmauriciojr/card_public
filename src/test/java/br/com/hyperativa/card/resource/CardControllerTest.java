package br.com.hyperativa.card.resource;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import br.com.hyperativa.card.exception.CardNotFoundException;
import br.com.hyperativa.card.exception.handler.CustomExceptionHandler;
import br.com.hyperativa.card.service.CardService;

@SpringBootTest
@AutoConfigureMockMvc
class CardControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    @BeforeEach
    void setup() {
    	 mockMvc = MockMvcBuilders
                 .standaloneSetup(cardController)
                 .setControllerAdvice(new CustomExceptionHandler())
                 .build();    }

    @Test
    void addCard_withValidCard_returnsSuccess() throws Exception {
        when(cardService.processAddCard("1234567890123456", null)).thenReturn("Card inserted/updated with ID: 1");

        mockMvc.perform(post("/card")
                .param("cardNumber", "1234567890123456")
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(content().string("Card inserted/updated with ID: 1"));

        verify(cardService, times(1)).processAddCard("1234567890123456", null);
    }

    @Test
    void getCard_withValidCard_returnsSuccess() throws Exception {
        when(cardService.findCard("1234567890123456")).thenReturn(1L);

        mockMvc.perform(get("/card/1234567890123456"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(cardService, times(1)).findCard("1234567890123456");
    }
    
    @Test
    void testGetCard_InvalidCardNumber_NotFound() throws Exception {
        // Given
        String invalidCardNumber = "9999999999999999";
        when(cardService.findCard(invalidCardNumber))
                .thenThrow(new CardNotFoundException("Card not found: " + invalidCardNumber));

        // When & Then
        mockMvc.perform(get("/card/{cardNumber}", invalidCardNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


}