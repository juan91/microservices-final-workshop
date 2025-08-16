package org.example.authservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String secret = "my-super-secret-key-which-should-be-very-long-to-work";

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Inyectamos el secret manualmente porque @Value no se aplica en pruebas unitarias
        jwtService.secret = secret;
    }

    @Test
    void testGenerateToken_ShouldReturnValidToken() {
        // Arrange
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(authentication.getName()).thenReturn("testUser");

        // Act
        String token = jwtService.generateToken(authentication);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());

        // Parseamos el token para validar los claims
        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        assertEquals("testUser", claims.getSubject());
        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiration());

        // Validamos que la expiración sea aproximadamente dentro de 24h
        Date expectedExpiration = Date.from(Instant.now().plusSeconds(86400));
        long diff = claims.getExpiration().getTime() - expectedExpiration.getTime();
        assertTrue(Math.abs(diff) < 2000, "Expiration should be within ~2s of 24h");
    }
}
