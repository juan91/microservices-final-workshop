package org.example.transactionservice.repo;

import org.example.transactionservice.model.Transactions;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TransaccionRepository extends ReactiveCrudRepository<Transactions, Long> {

    Flux<Transactions> findByCuentaOrigenIdOrCuentaDestinoIdOrderByFechaDesc(Long cuentaOrigenId, Long cuentaDestinoId);

}
