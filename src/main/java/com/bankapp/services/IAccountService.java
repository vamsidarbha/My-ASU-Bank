package com.bankapp.services;

import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;

public interface IAccountService {
    public Account getAccountByUser(User user);

    public Account getAccountByAccountId(String id);

    public Account saveAccount(Account account);

    public String updateBalance(Transaction transaction);
}
