package com.bankapp.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.PersonalIdentificationInfo;

public interface PIIRepository extends CrudRepository<PersonalIdentificationInfo, String>{
    PersonalIdentificationInfo findByEmail(String email); 
    List<PersonalIdentificationInfo> findByStatus(String status);
}
