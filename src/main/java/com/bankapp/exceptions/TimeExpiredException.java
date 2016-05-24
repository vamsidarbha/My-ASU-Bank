package com.bankapp.exceptions;

@SuppressWarnings("serial")
public class TimeExpiredException extends Throwable {

    public TimeExpiredException() {
        super();
    }

    public TimeExpiredException(final String message) {
        super(message);
    }

}