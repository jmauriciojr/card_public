package br.com.hyperativa.card.service;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import br.com.hyperativa.card.exception.CardNotFoundException;
import br.com.hyperativa.card.exception.CardProcessingException;
import br.com.hyperativa.card.model.Card;
import br.com.hyperativa.card.repository.CardRepository;
import br.com.hyperativa.card.service.impl.CardServiceImpl;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    private final String sampleCardNumber = "4456897999999999                            ";

    @BeforeEach
    public void setup() {
        // No additional setup is required.
    }

    @Test
    void testFindCard_Success() {
        // Prepare a dummy Card to be returned by the repository.
        Card dummyCard = new Card("dummyHash");
        dummyCard.setId(1L);
        when(cardRepository.findByCardHash(anyString()))
               .thenReturn(Optional.of(dummyCard));

        Long id = cardService.findCard(sampleCardNumber);
        assertNotNull(id);
        assertEquals(1L, id);
    }

    @Test
    void testFindCard_NotFound() {
        when(cardRepository.findByCardHash(anyString()))
               .thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> {
            cardService.findCard(sampleCardNumber);
        });
    }

    @Test
    void testProcessAddCard_WithCardNumber() {
        // Simulate that no card is found so that addCard will save a new card.
        when(cardRepository.findByCardHash(anyString()))
               .thenReturn(Optional.empty());
        when(cardRepository.save(any(Card.class)))
               .thenAnswer(invocation -> {
                   Card card = invocation.getArgument(0);
                   card.setId(2L);
                   return card;
               });

        String result = cardService.processAddCard(sampleCardNumber, null);
        assertNotNull(result);
        assertTrue(result.contains("Card inserted/updated with ID: 2"));
    }

    @Test
    void testProcessAddCard_WithFile() throws Exception {
        // Prepare a sample file content with a header, one record, and a trailer.
        String fileContent = "DESAFIO-HYPERATIVA           20180524LOTE0001000010\n" +
                             "C1     " + sampleCardNumber + "\n" +
                             "LOTE0001000010";
        InputStream is = new ByteArrayInputStream(fileContent.getBytes(StandardCharsets.UTF_8));
        MultipartFile multipartFile = new MockMultipartFile("file", "sample.txt", "text/plain", is);

        // For the record processed from the file, simulate that the card does not exist and a new one is saved.
        when(cardRepository.findByCardHash(anyString()))
               .thenReturn(Optional.empty());
        when(cardRepository.save(any(Card.class)))
               .thenAnswer(invocation -> {
                   Card card = invocation.getArgument(0);
                   card.setId(3L);
                   return card;
               });

        // Provide an empty cardNumber and a valid file to trigger file processing.
        String result = cardService.processAddCard("", multipartFile);
        assertNotNull(result);
        assertTrue(result.contains("Card " + sampleCardNumber.trim() + " inserted/updated with ID: 3"));
    }

    @Test
    void testProcessAddCard_InvalidRequest() {
        // Both cardNumber is empty and file is null; an exception is expected.
        assertThrows(CardProcessingException.class, () -> {
            cardService.processAddCard("", null);
        });
    }
    
    @Test
    void testProcessCardFile_IOException() throws Exception {
        // Create a mock MultipartFile that throws IOException when getInputStream is called
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        when(multipartFile.getInputStream()).thenThrow(new IOException("Simulated I/O error"));

        // Expecting CardProcessingException due to the simulated IOException
        assertThrows(CardProcessingException.class, () -> {
            cardService.processAddCard("", multipartFile);
        });

        // Verify that the error is logged
        verify(multipartFile, Mockito.times(1)).getInputStream();
    }
    
    @Test
    void testHashCard_NoSuchAlgorithmException() {
        try (MockedStatic<MessageDigest> mockedMessageDigest = mockStatic(MessageDigest.class)) {
            // Simulate NoSuchAlgorithmException when MessageDigest.getInstance is called
            mockedMessageDigest.when(() -> MessageDigest.getInstance("SHA-256"))
                    .thenThrow(new NoSuchAlgorithmException("Simulated error"));

            // Since hashCard is private, we call findCard which internally calls hashCard
            assertThrows(CardProcessingException.class, () -> {
                cardService.findCard("1234567890123456");
            });

            // Verify the method call
            mockedMessageDigest.verify(() -> MessageDigest.getInstance("SHA-256"), times(1));
        }
    }
}