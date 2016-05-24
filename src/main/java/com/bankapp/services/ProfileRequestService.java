package com.bankapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.constants.Constants;
import com.bankapp.models.ProfileRequest;
import com.bankapp.models.Role;
import com.bankapp.models.User;
import com.bankapp.repositories.ProfileRequestRepository;
import com.bankapp.repositories.RoleRepository;
import com.bankapp.repositories.UserRepository;

@Service
public class ProfileRequestService implements IProfileRequestService, Constants {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileRequestRepository profileRequestRepository;

    @Transactional
    @Override
    public String saveProfileRequest(ProfileRequest profile) {
        try {
            profileRequestRepository.save(profile);
            return SUCCESS;
        } catch (Exception e) {
            return ERROR;
        }
    }

    @Override
    public List<ProfileRequest> getPendingRequests() {
        Role manager = roleRepository.findByName("ROLE_MANAGER");
        Role employee = roleRepository.findByName("ROLE_EMPLOYEE");
        Role admin = roleRepository.findByName("ROLE_ADMIN");
        List<ProfileRequest> profileRequest = profileRequestRepository.findByStatusAndRole(S_PROFILE_UPDATE_PENDING,
                manager);
        profileRequest.addAll(profileRequestRepository.findByStatusAndRole(S_PROFILE_UPDATE_PENDING, employee));
        profileRequest.addAll(profileRequestRepository.findByStatusAndRole(S_PROFILE_UPDATE_PENDING, admin));
        return profileRequest;
    }

    @Override
    public void declineRequest(String id) {
        ProfileRequest profile = profileRequestRepository.findOne(id);
        profile.setStatus(S_PROFILE_UPDATE_DECLINED);
        profileRequestRepository.save(profile);

    }

    @Override
    public List<ProfileRequest> getRequestsByStatus(String status) {
        Role merchant = roleRepository.findByName("ROLE_MERCHANT");
        Role customer = roleRepository.findByName("ROLE_CUSTOMER");

        List<ProfileRequest> list = profileRequestRepository.findByStatusAndRole(status, merchant);
        list.addAll(profileRequestRepository.findByStatusAndRole(status, customer));
        return list;
    }

    @Override
    public ProfileRequest getRequestById(String id) {

        ProfileRequest request = profileRequestRepository.findOne(id);
        return request;
    }

    @Transactional
    @Override
    public String authorizeRequest(ProfileRequest request) {
        try {
            request.setStatus(S_PROFILE_UPDATE_VERIFIED);
            updateUser(request);
        } catch (Exception e) {
            return ERR_PROFILE_UPDATE;
        }

        return S_PROFILE_UPDATE_VERIFIED;
    }

    private User updateUser(ProfileRequest request) {
        User user = request.getUser();
        user.setAddress(request.getAddress());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setSecurityQuestion(request.getSecurityQuestion());
        user.setSecurityAnswer(request.getSercurityAnswer());
        profileRequestRepository.save(request);
        return userRepository.save(user);

    }

    @Transactional
    @Override
    public String declineRequest(ProfileRequest request) {
        try {
            request.setStatus(S_PROFILE_UPDATE_DECLINED);
            profileRequestRepository.save(request);
        } catch (Exception e) {
            return ERROR;
        }

        return S_PROFILE_UPDATE_DECLINED;
    }

    @Override
    public ProfileRequest getRequestByUser(User user) {
        ProfileRequest request = profileRequestRepository.findByUser(user);
        return request;
    }

}
