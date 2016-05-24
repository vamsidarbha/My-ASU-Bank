package com.bankapp.services;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.constants.Constants;
import com.bankapp.models.Account;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.repositories.AccountRepository;

@Service
public class AccountService implements IAccountService, Constants {

    private final Logger logger = Logger.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Transactional
    @Override
    public Account getAccountByUser(User user) {
        Account account = accountRepository.findByUser(user);

        String logMessageFormat = "[Action=%s][User=%s, Account=%s]";
        String logMessage = String.format(logMessageFormat, "getAccountByUser", user.getId(), account);
        logger.info(logMessage);
        
        return account;
    }

    @Transactional
    @Override
    public Account saveAccount(Account account) {
        try {
        Account acc = accountRepository.save(account);

        String logMessageFormat = "[Action=%s][Account=%]";
        String logMessage = String.format(logMessageFormat, "saveAccount", account.getAccId());
        logger.info(logMessage);

        return acc;
        } catch(Exception e) {
            return null;
        }
    }

    @Transactional
    @Override
    public String updateBalance(Transaction transaction) {
        try {
            double amount = transaction.getAmount();
            Account fromAccount = transaction.getFromAccount();
            Account toAccount = transaction.getToAccount();
            toAccount.setBalance(toAccount.getBalance() + amount);
            fromAccount.setBalance(fromAccount.getBalance() - amount);
            accountRepository.saveAndFlush(toAccount);
            accountRepository.saveAndFlush(fromAccount);

            String logMessageFormat = "[Action=%s][FromAccount=%s, ToAccount=%s, Transaction=%s]";
            String logMessage = String.format(logMessageFormat, "updateBalance", fromAccount.getAccId(),
                    toAccount.getAccId(), transaction.getTransactionId());
            logger.info(logMessage);

            return SUCCESS;
        } catch (Exception e) {
            String logMessageFormat = "[Action=%s][Transaction=%s, ErrorMessage=%s]";
            String logMessage = String.format(logMessageFormat, "updateBalance", transaction.getTransactionId(),
                    e.getMessage());
            logger.error(logMessage);
            return ERR_UNHANDLED;
        }
    }

    @Override
    public Account getAccountByAccountId(String id) {

        String logMessageFormat = "[Action=%s][Account=%]";
        String logMessage = String.format(logMessageFormat, "getAccountByAccountId", id);
        logger.info(logMessage);

        return accountRepository.findOne(id);
    }
}
