package com.bankapp.repositories;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.OneTimePassword;

public interface OTPRepository extends CrudRepository<OneTimePassword, String> {
    OneTimePassword findByValue(String value);
    OneTimePassword findByresourceIdAndResourceName(String resourceId, String resourceName);

}