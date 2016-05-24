package com.bankapp.exceptions;

@SuppressWarnings("serial")
public class UserNameExistsException extends Throwable {

    public UserNameExistsException(final String message) {
        super(message);
    }

}