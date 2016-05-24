package com.bankapp.services;

import java.util.List;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;

public interface ITransactionService {

    public List<Transaction> getTransactionsByAccount(Account fromAccount, Account toAccount);

    public String saveTransaction(String fromEmail, String toEmail, Transaction transaction);
    
    public String creditDebitTransaction(User user, String action, Transaction transaction);

    public String initiateTransaction(String fromEmail, String toEmail, Transaction transaction);

    public Transaction getTransactionsById(String id);

    public String askCustomerPayment(String fromEmail, String toEmail, Transaction transaction);

    public List<Transaction> getMerchantRequests(User user, String status);

    public String actionOnRequest(String id, String status);

    public List<Transaction> getPendingTransactions();
    
    public String creditDebit(String Email, Transaction transaction);
    
    public List<Transaction> getCreditDebitRequest();
    
    public String executeTransaction(Transaction transaction);

}