package org.example.bankservice.repo;

import org.example.bankservice.model.Bank;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface BankRepository extends ReactiveCrudRepository<Bank, Integer> {

}