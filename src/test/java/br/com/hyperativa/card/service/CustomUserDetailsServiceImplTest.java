package br.com.hyperativa.card.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import br.com.hyperativa.card.model.User;
import br.com.hyperativa.card.repository.UserRepository;
import br.com.hyperativa.card.service.impl.CustomUserDetailsServiceImpl;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsServiceImpl customUserDetailsService;

    @Test
    void loadUserByUsernameSuccessfully() {
        String usernameOrEmail = "user@example.com";
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setEnabled(true);

        when(userRepository.findByUsername(usernameOrEmail)).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(usernameOrEmail);

        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsernameNotFound() {
        String usernameOrEmail = "nonexistent@example.com";

        when(userRepository.findByUsername(usernameOrEmail)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(usernameOrEmail));
    }

    @Test
    void loadUserByUsernameDisabled() {
        String usernameOrEmail = "user@example.com";
        User user = new User();
        user.setUsername("user");
        user.setPassword("password");
        user.setEnabled(false);

        when(userRepository.findByUsername(usernameOrEmail)).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(usernameOrEmail);

        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertFalse(userDetails.isEnabled());
    }
}