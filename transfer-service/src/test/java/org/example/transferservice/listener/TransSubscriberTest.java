package org.example.transferservice.listener;

import org.example.transferservice.dto.Transactions;
import org.example.transferservice.repo.TransferRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransSubscriberTest {

    @Mock
    private TransferRepository transferRepository;

    private TransSubscriber transSubscriber;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transSubscriber = new TransSubscriber(transferRepository);

        // Simulamos que el repo siempre devuelve lo que recibe
        when(transferRepository.save(any(org.example.transferservice.model.Transactions.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
    }

    @Test
    void receiveTransfer_guardaTransaccionEnRepo() {

        Transactions input = Transactions.builder()
                .id(1L)
                .cuentaOrigenId(1001L)
                .cuentaDestinoId(2001L)
                .monto(BigDecimal.valueOf(500))
                .impuesto(BigDecimal.valueOf(5))
                .tipo("DEPOSITO")
                .fecha(LocalDateTime.now())
                .estado("COMPLETADA")
                .build();

        transSubscriber.receiveTransfer(input);

        ArgumentCaptor<org.example.transferservice.model.Transactions> captor =
                ArgumentCaptor.forClass(org.example.transferservice.model.Transactions.class);

        verify(transferRepository, times(1)).save(captor.capture());

        org.example.transferservice.model.Transactions saved = captor.getValue();
        assertThat(saved.getMonto()).isEqualByComparingTo(BigDecimal.valueOf(500));
        assertThat(saved.getImpuesto()).isEqualByComparingTo(BigDecimal.valueOf(5));
        assertThat(saved.getCuentaOrigenId()).isEqualTo(1001L);
        assertThat(saved.getCuentaDestinoId()).isEqualTo(2001L);
        assertThat(saved.getTipo()).isEqualTo("DEPOSITO"); // 👈 así como tu código lo setea
        assertThat(saved.getEstado()).isEqualTo("COMPLETADA");
    }
}
