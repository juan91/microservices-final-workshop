package org.example.authservice.controller;

import org.example.authservice.dto.AuthRequest;
import org.example.authservice.dto.AuthResponse;
import org.example.authservice.dto.RegisterRequest;
import org.example.authservice.service.JwtService;
import org.example.authservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ReactiveAuthenticationManager authenticationManager;

    @Test
    void login_success() {
        AuthRequest request = new AuthRequest("user", "password");
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "password");

        when(authenticationManager.authenticate(any()))
                .thenReturn(Mono.just(auth));
        when(jwtService.generateToken(auth))
                .thenReturn("mocked-jwt");

        webTestClient.post()
                .uri("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(AuthResponse.class)
                .value(resp -> {
                    assert resp.token().equals("mocked-jwt");
                });
    }

    @Test
    void login_unauthorized() {
        AuthRequest request = new AuthRequest("baduser", "badpassword");

        when(authenticationManager.authenticate(any()))
                .thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/api/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void register_success() {
        RegisterRequest registerRequest = new RegisterRequest("user", "password", "email@test.com");

        when(userService.register(registerRequest))
                .thenReturn(Mono.just("User registered"));

        webTestClient.post()
                .uri("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(registerRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("User registered");
    }
}
