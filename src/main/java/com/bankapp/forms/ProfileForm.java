package com.bankapp.forms;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import com.bankapp.validators.ValidEmail;

public class ProfileForm {

    @Size(min = 1, max = 50)
    String username;

    @ValidEmail
    @Size(min = 1, max = 50)
    String email;

    @NotNull
    @Size(min = 1, max = 50)
    String address;

    @NotBlank
    @Size(min = 1, max = 50)
    String securityQuestion;

    @NotBlank
    @Size(min = 1, max = 50)
    String securityAnswer;

    @NotNull
    @Pattern(regexp = "\\d{10}", message = "Please enter a valid 10 digit phone number")
    String phoneNumber;

    @NotNull
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Past
    Date dateOfBirth;

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
}