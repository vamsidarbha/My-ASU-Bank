
package com.bankapp.configs;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.models.Account;
import com.bankapp.models.Role;
import com.bankapp.models.User;
import com.bankapp.repositories.AccountRepository;
import com.bankapp.repositories.RoleRepository;
import com.bankapp.repositories.UserRepository;

@Component
public class DataSetup implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${com.bankapp.data.doSetup}")
    private Boolean doDataSetup;

    @Value("${com.bankapp.data.roleSetup}")
    private Boolean doRoleSetup;

    @Value("${com.bankapp.data.deploy}")
    private Boolean deploy;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        if (doRoleSetup) {
            // Create initial roles
            for (Roles role : Roles.values()) {
                createRoleIfNotFound(role.toString());
            }
        }

        if (!doDataSetup) {
            return;
        }

        final Role adminRole = roleRepository.findByName("ROLE_ADMIN");

        final String adminEmail = "test@admin.com";
        final String managerEmail = "test@manager.com";
        final String employeeEmail = "test@employee.com";
        final String customerEmail = "test@customer.com";
        final String merchantEmail = "test@merchant.com";
        final String agencyEmail = "test@agency.com";

        final User adminUser = new User();
        adminUser.setUsername("Test Admin");
        adminUser.setPassword(passwordEncoder.encode("test123"));
        adminUser.setEmail(adminEmail);
        adminUser.setRole(adminRole);
        adminUser.setEnabled(true);
        adminUser.setDateOfBirth(new Date());
        adminUser.setPhoneNumber("1000000000");
        adminUser.setSecurityQuestion("What's your name?");
        adminUser.setSecurityAnswer("admin");
        createUserIfNotFound(adminEmail, adminUser);

        final Role agencyRole = roleRepository.findByName("ROLE_AGENCY");
        User agencyUser = new User();
        agencyUser.setUsername("Test Agency");
        agencyUser.setPassword(passwordEncoder.encode("test123"));
        agencyUser.setEmail(agencyEmail);
        agencyUser.setRole(agencyRole);
        agencyUser.setEnabled(true);
        agencyUser.setDateOfBirth(new Date());
        agencyUser.setPhoneNumber("1000000000");
        agencyUser.setSecurityQuestion("What's your name?");
        agencyUser.setSecurityAnswer("agency");
        createUserIfNotFound(agencyEmail, agencyUser);

        if (!deploy) {
            final Role employeeRole = roleRepository.findByName("ROLE_EMPLOYEE");
            User employeeUser = new User();
            employeeUser.setUsername("Test Employee");
            employeeUser.setPassword(passwordEncoder.encode("test123"));
            employeeUser.setEmail(employeeEmail);
            employeeUser.setRole(employeeRole);
            employeeUser.setEnabled(true);
            employeeUser.setDateOfBirth(new Date());
            employeeUser.setPhoneNumber("1231231231");
            employeeUser.setSecurityQuestion("Name?");
            employeeUser.setSecurityAnswer("test");
            createUserIfNotFound(employeeEmail, employeeUser);

            final Role managerRole = roleRepository.findByName("ROLE_MANAGER");
            User managerUser = new User();
            managerUser.setUsername("Test Manager");
            managerUser.setPassword(passwordEncoder.encode("test123"));
            managerUser.setEmail(managerEmail);
            managerUser.setRole(managerRole);
            managerUser.setEnabled(true);
            managerUser.setDateOfBirth(new Date());
            managerUser.setPhoneNumber("1231231231");
            managerUser.setSecurityQuestion("Name?");
            managerUser.setSecurityAnswer("test");
            createUserIfNotFound(managerEmail, managerUser);

            final Role customerRole = roleRepository.findByName("ROLE_CUSTOMER");
            User customerUser = new User();
            customerUser.setUsername("Test Customer");
            customerUser.setPassword(passwordEncoder.encode("test123"));
            customerUser.setEmail(customerEmail);
            customerUser.setRole(customerRole);
            customerUser.setEnabled(true);
            customerUser.setDateOfBirth(new Date());
            customerUser.setPhoneNumber("1231231231");
            customerUser.setSecurityQuestion("Name?");
            customerUser.setSecurityAnswer("test");
            createUserIfNotFound(customerEmail, customerUser);

            final Role merchantRole = roleRepository.findByName("ROLE_MERCHANT");
            User merchantUser = new User();
            merchantUser.setUsername("Test Merchant");
            merchantUser.setPassword(passwordEncoder.encode("test123"));
            merchantUser.setEmail(merchantEmail);
            merchantUser.setRole(merchantRole);
            merchantUser.setEnabled(true);
            merchantUser.setDateOfBirth(new Date());
            merchantUser.setPhoneNumber("1231231231");
            merchantUser.setSecurityQuestion("Name?");
            merchantUser.setSecurityAnswer("test");
            createUserIfNotFound(merchantEmail, merchantUser);
        }

        doDataSetup = true;
    }

    @Transactional
    private final Role createRoleIfNotFound(final String name) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role(name);
            roleRepository.save(role);
        }
        return role;
    }

    @Transactional
    private final void createUserIfNotFound(final String email, final User user) {
        User existingUser = userRepository.findByEmail(email);
        if (existingUser == null) {
            userRepository.save(user);
            switch (user.getRole().getName()) {
            case "ROLE_CUSTOMER":
            case "ROLE_MERCHANT":
                createAccount(user);
                break;
            }
        }
    }

    @Transactional
    private final void createAccount(final User user) {
        Account account = new Account();
        user.setAccount(account);
        account.setUser(user);
        account.setCreated(new Date());
        account.setTypeOfAccount("Savings");
        account.setBalance(100.0);
        account.setCriticalLimit(100.0);
        accountRepository.save(account);
        userRepository.save(user);
    }

}