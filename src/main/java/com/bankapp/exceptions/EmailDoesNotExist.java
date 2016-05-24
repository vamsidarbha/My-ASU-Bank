package com.bankapp.exceptions;

@SuppressWarnings("serial")
public class EmailDoesNotExist extends Throwable {

    public EmailDoesNotExist(final String message) {
        super(message);
    }

}