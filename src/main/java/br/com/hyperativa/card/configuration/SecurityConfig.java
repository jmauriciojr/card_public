package br.com.hyperativa.card.configuration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import br.com.hyperativa.card.security.JwtAuthenticationEntryPoint;
import br.com.hyperativa.card.security.JwtAuthenticationFilter;
import br.com.hyperativa.card.security.JwtTokenProvider;
import br.com.hyperativa.card.service.impl.CustomUserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtTokenProvider tokenProvider;
    
    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter(CustomUserDetailsServiceImpl customUserDetailsService) {
        return new JwtAuthenticationFilter(tokenProvider, customUserDetailsService);
    }
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
         return authenticationConfiguration.getAuthenticationManager();
    }
    
    @Bean
     WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/swagger-ui/**", "/v3/api-docs/**"
        );
    }
    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
          .cors(Customizer.withDefaults())
          .csrf(csrf -> csrf.disable())
          .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(unauthorizedHandler))
          .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
          .authorizeHttpRequests(authorize -> authorize

             .requestMatchers("/auth/**").permitAll()
             .anyRequest().authenticated()
          );
        
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}