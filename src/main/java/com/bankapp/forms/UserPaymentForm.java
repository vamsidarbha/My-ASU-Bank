package com.bankapp.forms;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import com.bankapp.validators.ValidEmail;

public class UserPaymentForm {

    @NotNull
    @ValidEmail
    @NotEmpty
    @NotBlank
    @Size(min = 1, max = 50)
    String email;

    @NotNull
    String amount;

    @Size(min = 1, max = 50)
    String comment;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

}