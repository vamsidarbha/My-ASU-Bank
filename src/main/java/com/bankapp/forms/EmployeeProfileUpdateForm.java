package com.bankapp.forms;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

public class EmployeeProfileUpdateForm {

    @NotNull
    private String Id;

    @NotEmpty
    @Size(min = 1, max = 50)
    private String username;

    @Size(min = 1, max = 50)
    private String address;

    @Pattern(regexp="\\d{10}", message = "Please enter a valid 10 digit phone number")
    private String phoneNumber;

    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Past
    @NotNull
    private Date dateOfBirth;

    @Size(min = 1, max = 50)
    private String gender;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

}
