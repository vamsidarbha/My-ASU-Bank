package com.bankapp.services;

import java.util.List;

import com.bankapp.models.PiiRequest;

public interface IPIIRequestService {
    
    public String saveRequest(PiiRequest piiRequest);
    public List<PiiRequest> getPiiAdminRequest();
    public String authorize(PiiRequest piiRequest);
    public String decline(PiiRequest request); 

}
