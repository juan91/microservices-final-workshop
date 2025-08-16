package org.example.authservice.service;

import org.example.authservice.model.User;
import org.example.authservice.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class UserDetailServiceTest {

    private IUserRepository repository;
    private UserDetailService userDetailService;

    @BeforeEach
    void setUp() {
        repository = mock(IUserRepository.class);
        userDetailService = new UserDetailService(repository);
    }

    @Test
    void testFindByUsername_UserFound() {
        // given
        String username = "john";
        User userEntity = new User();
        userEntity.setUsername(username);
        userEntity.setPassword("password123");
        userEntity.setRole("USER");

        when(repository.findByUsername(username)).thenReturn(Mono.just(userEntity));

        // when - then
        StepVerifier.create(userDetailService.findByUsername(username))
                .expectNextMatches(userDetails ->
                        userDetails.getUsername().equals("john")
                                && userDetails.getPassword().equals("password123")
                                && userDetails.getAuthorities().stream().anyMatch(
                                auth -> auth.getAuthority().equals("ROLE_USER"))
                )
                .verifyComplete();

        verify(repository, times(1)).findByUsername(username);
    }

    @Test
    void testFindByUsername_UserNotFound() {
        // given
        String username = "unknown";
        when(repository.findByUsername(username)).thenReturn(Mono.empty());

        // when - then
        StepVerifier.create(userDetailService.findByUsername(username))
                .expectComplete()
                .verify();

        verify(repository, times(1)).findByUsername(username);
    }
}
