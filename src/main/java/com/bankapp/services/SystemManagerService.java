package com.bankapp.services;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bankapp.constants.Constants;
import com.bankapp.models.Account;
import com.bankapp.models.ProfileRequest;
import com.bankapp.models.Transaction;
import com.bankapp.models.User;
import com.bankapp.repositories.AccountRepository;
import com.bankapp.repositories.ProfileRequestRepository;
import com.bankapp.repositories.TransactionRepository;
import com.bankapp.repositories.UserRepository;

@Service
public class SystemManagerService implements ISystemManagerService, Constants {

    @Autowired
    private UserRepository UserRepo;

    @Autowired
    private TransactionRepository TransRepo;

    @Autowired
    private AccountRepository AccountRepo;
    
    @Autowired
    private ProfileRequestRepository ProfileRepo;
    


    @Override
    public List<Transaction> getTransactionsByStatus(String status) {

        List<Transaction> list = TransRepo.findByStatus(status);
        //System.out.println(list);
        return list;
    }

    @Override
    public User viewUserById(String id) {
        User user = UserRepo.findById(id);
        return user;
    }

    @Override
    public User viewUserByEmail(String email) {
        User user = UserRepo.findByEmail(email);
        return user;
    }

    @Override
    public User addUser(User user) {
        UserRepo.save(user);
        return user;
    }

    public String approveTransaction(Transaction transaction) {

        String result = "";
        transaction.setStatus(S_OTP_VERIFIED);
        try {
            TransRepo.save(transaction);
            result = SUCCESS;
            //System.out.println("Done approve");
        } catch (Exception e) {
            result = ERROR;
        }
        return result;
    }

    @Override
    public Transaction getTransactionById(String id) {
        Transaction transaction = TransRepo.findOne(id);
        return transaction;
    }

    public String reflectChangesToSender(Account account, Double balance, Double amount) {
        try {
            account.setBalance(balance - amount);
            AccountRepo.save(account);

        } catch (Exception e) {
            return e.getMessage();
        }

        return "Success";
    }

    public String reflectChangesToReceiver(Account account, Double balance, Double amount) {
        try {
            account.setBalance(balance + amount);
            AccountRepo.save(account);
        } catch (Exception e) {
            return e.getMessage();
        }

        return "Success";
    }
    
    
   

    public String declineTransaction(Transaction transaction) {

        String result = "";
        transaction.setStatus(S_DECLINED);
        TransRepo.save(transaction);
        result = "Transaction has been declined";
      //  System.out.println("Not approved");

        return result;
    }

    public String modifyTransaction(Transaction transaction, Date new_Date) {

        String result = "";
        transaction.setStatus(S_VERIFIED);
        transaction.setTransferDate(new_Date);
        TransRepo.save(transaction);
        result = "Transaction has been modified";
    //    System.out.println("Done modified");

        return result;
    }
    
    public String saveUser(User user)
    {
    	String str = "";
    	try {
    	UserRepo.save(user);
    	str = "Success";
    	}catch(Exception e)
    	{
    		str = "Error";
    	}
    	return str;
    }

	@Override
	public String approveProfileRequest(ProfileRequest request) {
		// TODO Auto-generated method stub
		String result= "";
		request.setStatus(S_PROFILE_UPDATE_VERIFIED);
		
		 try {
	            ProfileRepo.save(request);
	            result = "success";
	            //System.out.println("Done approve");
	        } catch (Exception e) {
	            result = "error";
	        }

	        return result;
		
	}

	

	@Override
	public ProfileRequest getProfilebRequestByRId(String id) {
		// TODO Auto-generated method stub
        ProfileRequest Request = ProfileRepo.findOne(id);

		return Request;
	}
}