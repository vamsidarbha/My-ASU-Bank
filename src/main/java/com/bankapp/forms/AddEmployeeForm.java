package com.bankapp.forms;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NotFound;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import com.bankapp.models.Role;
import com.bankapp.validators.ValidEmail;

public class AddEmployeeForm {
    
    @NotEmpty    
    @Size(min = 1, max = 50)
    private String username;
    
    @NotEmpty
    @NotFound
    @ValidEmail
    @Size(min = 1, max = 50)
    private String email;
    
    Role role;
    
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @NotNull(message = "Please provide a date of birth.")
    @Past
    private Date dateOfBirth;

    public AddEmployeeForm() {
        this.role = new Role();
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

}
