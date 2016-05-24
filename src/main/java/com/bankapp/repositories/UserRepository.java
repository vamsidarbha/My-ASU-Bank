package com.bankapp.repositories;


import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


import com.bankapp.models.Role;
import com.bankapp.models.User;

@Repository
@Qualifier(value = "userRepository")
public interface UserRepository extends CrudRepository<User, String> {
    public User findByEmail(String email);

    public User findById(String id);

    public User findByUsername(String name);

    public List<User> findByRole(Role role);
    
    public List<User> findByIsDeleted(boolean value);

}