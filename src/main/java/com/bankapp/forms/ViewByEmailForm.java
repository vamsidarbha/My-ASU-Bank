package com.bankapp.forms;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;

import com.bankapp.validators.ValidEmail;

public class ViewByEmailForm {
    @NotBlank
    @ValidEmail
    @Size(min = 1, max = 50)
    String email;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}