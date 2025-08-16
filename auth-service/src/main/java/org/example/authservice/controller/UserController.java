package org.example.authservice.controller;

import org.example.authservice.dto.AuthRequest;
import org.example.authservice.dto.AuthResponse;
import org.example.authservice.dto.RegisterRequest;
import org.example.authservice.model.User;
import org.example.authservice.service.JwtService;
import org.example.authservice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/auth")
public class UserController {
  private final JwtService jwtService;
  private final UserService userService;
  private final ReactiveAuthenticationManager authenticationManager;

  public UserController(JwtService jwtService, UserService userService, ReactiveAuthenticationManager authenticationManager) {
    this.jwtService = jwtService;
    this.userService = userService;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("")
  public Mono<ResponseEntity<AuthResponse>> login(@RequestBody AuthRequest request) {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.username(), request.password());

    return authenticationManager.authenticate(authenticationToken)
      .map(authentication -> {
        String token = jwtService.generateToken(authentication);
        return ResponseEntity.ok(new AuthResponse(token));
      })
      .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
  }

  @PostMapping("/register")
  public Mono<ResponseEntity<Object>> register(@RequestBody RegisterRequest request) {
    return userService.register(request)
      .map(ResponseEntity::ok);
  }
}
