package com.bankapp.configs;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.bankapp.services.LoginAttemptService;

public class CustomDaoAuthenticationProvider extends DaoAuthenticationProvider {
    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    HttpServletRequest request;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Could assert pre-conditions here, e.g. rate-limiting
        // and throw a custom AuthenticationException if necessary

        try {
            return super.authenticate(authentication);
        } catch (BadCredentialsException e) {
            // Will throw a custom exception if too many failed logins have
            // occurred
            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
            loginAttemptService.loginFailed(ipAddress);
            throw e;
        }
    }
}