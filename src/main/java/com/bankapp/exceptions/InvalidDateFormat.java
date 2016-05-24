package com.bankapp.exceptions;

@SuppressWarnings("serial")
public class InvalidDateFormat extends Throwable {

    public InvalidDateFormat(final String message) {
        super(message);
    }

}