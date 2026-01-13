package com.banking.ledger.domain.repository;

import com.banking.ledger.domain.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    // We can use optimistic locking (@Version in entity) or pessimistic locking
    // here.
    // Given the requirement for concurrency control, using Spring Data's @Lock with
    // PESSIMISTIC_WRITE
    // is an option if we wanted to lock rows, but the requirements mention
    // "Optimistic locking".
    // So we rely on the @Version field in the Account entity and standard JPA
    // behavior.

    Optional<Account> findById(UUID id);
}
