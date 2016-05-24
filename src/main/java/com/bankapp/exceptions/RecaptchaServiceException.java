package com.bankapp.exceptions;

import org.springframework.web.client.RestClientException;

public class RecaptchaServiceException extends Exception {

    private static final long serialVersionUID = -8117877279107289743L;

    public RecaptchaServiceException(String message, RestClientException cause) {
        super(message, cause);
    }

}
