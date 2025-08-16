package org.example.apigateway.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private String secret = "mysecretkeymysecretkeymysecretkey123";
    private Key signingKey;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secret", secret);
        signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    private String generateToken(long expirationMillis) {
        return Jwts.builder()
                .setSubject("testUser")
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void testValidToken() {
        String token = generateToken(10000);
        boolean result = jwtService.isValidToken(token);
        assertTrue(result, "El token debería ser válido");
    }

    @Test
    void testExpiredToken() {
        String token = generateToken(-10000);
        assertThrows(io.jsonwebtoken.ExpiredJwtException.class,
                () -> jwtService.isValidToken(token),
                "El token expirado debería lanzar ExpiredJwtException");
    }

    @Test
    void testInvalidSignatureToken() {
        // creamos un token con otro secret
        Key otherKey = Keys.hmacShaKeyFor("otherSecretKeyotherSecretKey123456".getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .setSubject("testUser")
                .setExpiration(new Date(System.currentTimeMillis() + 10000))
                .signWith(otherKey, SignatureAlgorithm.HS256)
                .compact();

        assertThrows(Exception.class, () -> jwtService.isValidToken(token),
                "Un token con firma inválida debería lanzar excepción");
    }
}
