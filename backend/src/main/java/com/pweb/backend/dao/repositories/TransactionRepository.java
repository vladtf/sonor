package com.pweb.backend.dao.repositories;

import com.pweb.backend.dao.entities.Account;
import com.pweb.backend.dao.entities.Transaction;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

    List<Transaction> findAllByDestAccount(Account destAccount);

    List<Transaction> findAllBySourceAccount(Account sourceAccount);
}
