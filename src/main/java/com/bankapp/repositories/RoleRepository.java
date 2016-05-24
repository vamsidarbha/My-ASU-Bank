package com.bankapp.repositories;

import org.springframework.data.repository.CrudRepository;

import com.bankapp.models.Role;

public interface RoleRepository extends CrudRepository<Role, String> {

    Role findByName(String name);

}