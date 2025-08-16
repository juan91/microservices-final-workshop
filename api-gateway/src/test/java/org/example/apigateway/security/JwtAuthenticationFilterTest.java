package org.example.apigateway.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private JwtAuthenticationFilter filter;
    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {
        jwtService = mock(JwtService.class);
        filter = new JwtAuthenticationFilter(jwtService);
        chain = mock(GatewayFilterChain.class);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    void testNoAuthorizationHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
        verify(chain, never()).filter(any());
    }

    @Test
    void testInvalidAuthorizationHeader() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header(HttpHeaders.AUTHORIZATION, "InvalidToken")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
        verify(chain, never()).filter(any());
    }

    @Test
    void testInvalidToken() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer badtoken")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtService.isValidToken("badtoken")).thenReturn(false);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
        verify(chain, never()).filter(any());
    }

    @Test
    void testValidToken() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer goodtoken")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtService.isValidToken("goodtoken")).thenReturn(true);

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assert exchange.getResponse().getStatusCode() == null; // no UNAUTHORIZED
        verify(chain, times(1)).filter(any());
    }

    @Test
    void testExceptionInJwtService() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .header(HttpHeaders.AUTHORIZATION, "Bearer sometoken")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtService.isValidToken("sometoken")).thenThrow(new RuntimeException("JWT error"));

        StepVerifier.create(filter.filter(exchange, chain))
                .verifyComplete();

        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
        verify(chain, never()).filter(any());
    }
}
