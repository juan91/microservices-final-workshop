package org.example.authservice.service;

import org.example.authservice.dto.RegisterRequest;
import org.example.authservice.model.User;
import org.example.authservice.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private IUserRepository repository;
    private PasswordEncoder encoder;
    private UserService userService;

    @BeforeEach
    void setup() {
        repository = Mockito.mock(IUserRepository.class);
        encoder = Mockito.mock(PasswordEncoder.class);
        userService = new UserService(repository, encoder);
    }

    @Test
    void shouldRegisterNewUserWhenNotExists() {
        RegisterRequest request = new RegisterRequest("newUser", "plainPass", "USER");
        User savedUser = new User(1L, request.username(), "encodedPass", request.role());

        // repository.findByUsername devuelve vacío → no existe el usuario
        when(repository.findByUsername(request.username())).thenReturn(Mono.empty());
        // encoder.encode debe codificar la contraseña
        when(encoder.encode(request.password())).thenReturn("encodedPass");
        // repository.save debe devolver el user guardado
        when(repository.save(any(User.class))).thenReturn(Mono.just(savedUser));

        StepVerifier.create(userService.register(request))
                .expectNext(savedUser)
                .verifyComplete();

        verify(repository).findByUsername(request.username());
        verify(encoder).encode(request.password());
        verify(repository).save(any(User.class));
    }


}
