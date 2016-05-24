package com.bankapp.services;

import com.bankapp.exceptions.RecaptchaServiceException;

public interface IRecaptchaService {

    boolean isResponseValid(String remoteIp, String response) throws RecaptchaServiceException;

}