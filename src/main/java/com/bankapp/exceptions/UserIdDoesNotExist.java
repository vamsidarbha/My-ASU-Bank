package com.bankapp.exceptions;

@SuppressWarnings("serial")
public class UserIdDoesNotExist extends Throwable {

    public UserIdDoesNotExist(final String message) {
        super(message);
    }

}