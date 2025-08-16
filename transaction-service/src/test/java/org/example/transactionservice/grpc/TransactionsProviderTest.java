package org.example.transactionservice.grpc;

import io.grpc.stub.StreamObserver;
import org.example.transactionservice.model.Transactions;
import org.example.transactionservice.repo.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionsProviderTest {

    @Mock
    private TransaccionRepository repository;

    @Mock
    private StreamObserver<TransactionListResponse> responseObserver;

    private TransactionsProvider transactionsProvider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transactionsProvider = new TransactionsProvider(repository);
    }

    @Test
    void testGetTransaction_success() {
        // Arrange
        TransactionRequest request = TransactionRequest.newBuilder()
                .setTransactionId(1L)
                .build();

        Transactions tx1 = Transactions.builder()
                .id(1L)
                .cuentaOrigenId(1L)
                .cuentaDestinoId(2L)
                .monto(new BigDecimal("200"))
                .tipo("DEBITO")
                .fecha(LocalDateTime.now())
                .estado("OK")
                .build();

        when(repository.findByCuentaOrigenIdOrCuentaDestinoIdOrderByFechaDesc(1L, 1L))
                .thenReturn(Flux.just(tx1));

        ArgumentCaptor<TransactionListResponse> captor = ArgumentCaptor.forClass(TransactionListResponse.class);

        // Act
        transactionsProvider.getTransaction(request, responseObserver);

        // Assert
        verify(responseObserver, times(1)).onNext(captor.capture());
        verify(responseObserver, times(1)).onCompleted();

        TransactionListResponse response = captor.getValue();
        assertEquals(1, response.getTransactionsCount());

        TransactionResponse txResponse = response.getTransactions(0);
        assertEquals("DEBITO", txResponse.getTipo());
        assertEquals("OK", txResponse.getEstado());
        assertEquals("200", txResponse.getMonto());
    }

    @Test
    void testGetTransaction_error() {
        // Arrange
        TransactionRequest request = TransactionRequest.newBuilder()
                .setTransactionId(99L)
                .build();

        RuntimeException ex = new RuntimeException("DB error");
        when(repository.findByCuentaOrigenIdOrCuentaDestinoIdOrderByFechaDesc(99L, 99L))
                .thenReturn(Flux.error(ex));

        // Act
        transactionsProvider.getTransaction(request, responseObserver);

        // Assert
        verify(responseObserver, times(1)).onError(ex);
        verify(responseObserver, never()).onCompleted();
    }
}
