package com.bankapp.services;

import java.security.Principal;
import java.util.List;

import com.bankapp.exceptions.EmailExistsException;
import com.bankapp.models.OneTimePassword;
import com.bankapp.models.User;
import com.bankapp.models.VerificationToken;

public interface IUserService {

    public String addEmployee(User user, String roleName) throws EmailExistsException;

    User registerNewUserAccount(User accountDto, String roleName) throws EmailExistsException;

    User getUserById(String id);

    User getUserByEmail(String email);

    User getUserByVerificationToken(String verificationToken);

    User getUserFromSession(Principal principal);

    public boolean hasMissingFields(Principal principal);

    String saveUser(User user);

    void saveRegisteredUser(User user);

    void createVerificationToken(User user, String token);

    VerificationToken getVerificationToken(String VerificationToken);

    VerificationToken generateNewVerificationToken(String VerificationToken);

    String updateUser(String existingUserId, User newUser);

    boolean markUserAsDeleted(String email);

    void deleteUser(User user);

    String deleteExternalUser(String email);

    List<User> getManagers();

    List<User> getEmployees();

    List<User> getCustomers();

    List<User> getMerchants();

    void generateTemporaryPassword(User user);

    boolean changePassword(User user);

    boolean emailExist(String email);

    boolean idExist(String id);

    public boolean verifyPassword(User user, String currentPassword);

    // OTP Section
    OneTimePassword generateOTP(String resourceId, String resourceName);

    OneTimePassword generateNewOTP(String value);

    boolean verifyOTP(String otp, String id, String name);

    List<User> displayDeleteUsers();

}