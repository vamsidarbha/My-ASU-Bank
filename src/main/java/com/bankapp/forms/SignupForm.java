package com.bankapp.forms;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import com.bankapp.models.Role;
import com.bankapp.validators.ValidEmail;
import com.bankapp.validators.ValidPassword;

public class SignupForm extends RecaptchaForm {
    @NotEmpty
    @Size(min = 1, max = 50)
    private String username;

    @ValidEmail
    @Size(min = 1, max = 50)
    private String email;

    @ValidPassword
    private String password;

    @Size(min = 1, max = 50)
    private String address;

    @Pattern(regexp = "\\d{10}", message = "Please enter a valid 10 digit phone number")
    private String phoneNumber;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @NotNull(message = "Please provide a date of birth.")
    @Past
    private Date dateOfBirth;

    @Size(min = 1, max = 50)
    private String gender;

    @Size(min = 1, max = 100)
    private String securityQuestion;

    @Size(min = 1, max = 50)
    private String securityAnswer;

    Role role;

    @Size(min = 1, max = 50)
    private String typeOfAccount;

    public SignupForm() {
        this.role = new Role();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getTypeOfAccount() {
        return typeOfAccount;
    }

    public void setTypeOfAccount(String typeOfAccount) {
        this.typeOfAccount = typeOfAccount;
    }

}
