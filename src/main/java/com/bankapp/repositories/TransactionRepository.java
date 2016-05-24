package com.bankapp.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;

public interface TransactionRepository extends CrudRepository<Transaction, String> {

    List<Transaction> findByFromAccountOrToAccountOrderByCreatedDesc(Account fromAccount, Account toAccount);

    Transaction findByTransactionId(String id);

    Transaction saveAndFlush(Transaction transaction);

    List<Transaction> findByFromAccount(Account fromAccount);

    List<Transaction> findByToAccount(Account toAccount);

    List<Transaction> findByStatus(String status);

    List<Transaction> findByStatusNot(String status);

    List<Transaction> findByStatusAndFromAccount(String status, Account account);
}
