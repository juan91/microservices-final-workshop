package org.example.transferservice.repo;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends ReactiveCrudRepository<org.example.transferservice.model.Transactions, Long> {
}
