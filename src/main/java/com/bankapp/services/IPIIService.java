package com.bankapp.services;

import java.util.List;

import com.bankapp.models.PersonalIdentificationInfo;

public interface IPIIService {
    public String savePII(PersonalIdentificationInfo pii);
    public List<PersonalIdentificationInfo> getAuthorizedPII();
    public List<PersonalIdentificationInfo> getPiiInfo();
}
