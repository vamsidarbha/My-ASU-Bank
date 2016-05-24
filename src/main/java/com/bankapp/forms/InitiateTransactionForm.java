package com.bankapp.forms;

import java.util.Date;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

public class InitiateTransactionForm {

    @NotNull
    @Size(min = 1, max = 50)
    String email;

    @NotNull
    String amount;

    @Size(min = 1, max = 50)
    String comment;

    @NotNull
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    @Future
    Date transferDate;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String  amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(Date transferDate) {
        this.transferDate = transferDate;
    }
}
