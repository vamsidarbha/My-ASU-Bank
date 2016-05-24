package com.bankapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.constants.Constants;
import com.bankapp.models.PersonalIdentificationInfo;
import com.bankapp.repositories.PIIRepository;
import com.google.common.collect.Lists;

@Service
public class PIIService implements IPIIService, Constants {

    @Autowired
    PIIRepository piiRepository;

    @Transactional
    @Override
    public String savePII(PersonalIdentificationInfo pii) {
        PersonalIdentificationInfo tempPii = checkIfExists(pii);
        try {
            if (tempPii != null) {
                piiRepository.save(tempPii);
            } else {
                piiRepository.save(pii);
            }
        } catch (Exception e) {
            return ERROR;
        }
        return SUCCESS;
    }

    private PersonalIdentificationInfo checkIfExists(PersonalIdentificationInfo pii) {
        PersonalIdentificationInfo tempPii = piiRepository.findByEmail(pii.getEmail());
        if (tempPii != null) {
            tempPii.setPii(pii.getPii());
        }
        return tempPii;
    }

    @Override
    public List<PersonalIdentificationInfo> getAuthorizedPII() {
        List<PersonalIdentificationInfo> piiInfo = piiRepository.findByStatus(S_PII_AUTHORIZED);
        return piiInfo;
    }

    @Override
    public List<PersonalIdentificationInfo> getPiiInfo() {
        List<PersonalIdentificationInfo> piiInfo = Lists.newArrayList(piiRepository.findAll());
        return piiInfo;
    }

}
