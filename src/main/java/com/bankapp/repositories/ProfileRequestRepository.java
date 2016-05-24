package com.bankapp.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.ProfileRequest;
import com.bankapp.models.Role;
import com.bankapp.models.User;

public interface ProfileRequestRepository extends CrudRepository<ProfileRequest, String> {

    List<ProfileRequest> findByStatusAndRole(String status, Role role);

    ProfileRequest findByRole(Role role);

    ProfileRequest findByUser(User user);
}
