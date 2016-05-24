package com.bankapp.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankapp.models.Role;
import com.bankapp.repositories.RoleRepository;
import com.bankapp.repositories.UserRepository;

@Service("userDetailsService")
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LoginAttemptService loginAttemptService;

    public CustomUserDetailsService() {
        super();
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        String ip = request.getRemoteAddr();
        if (loginAttemptService.isBlocked(ip)) {
            throw new RuntimeException("blocked");
        }

        boolean enabled = true;
        boolean accountNotExpired = true;
        boolean credentialsNotExpired = true;
        boolean accountNotLocked = true;
        try {
            final com.bankapp.models.User user = userRepository.findByEmail(email);
            if (user == null) {
                return new User(" ", " ", enabled, true, true, true,
                        getAuthorities(roleRepository.findByName("ROLE_CUSTOMER")));
            }

            saveLoginDetails(user);

            return new User(user.getEmail(), user.getPassword(), user.isEnabled(), accountNotExpired,
                    credentialsNotExpired, accountNotLocked, getAuthorities(user.getRole()));
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final Collection<? extends GrantedAuthority> getAuthorities(final Role role) {
        final List<SimpleGrantedAuthority> authorities = new ArrayList<SimpleGrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(role.getName()));
        return authorities;
    }

    private void saveLoginDetails(com.bankapp.models.User user) {

        String ip = request.getRemoteAddr();
        String lastLoginIP = user.getCurrentLoginIP();
        Date lastLoginDate = user.getCurrentLoginDate();
        user.setLastLoginIP(lastLoginIP);
        user.setLastLoginDate(lastLoginDate);
        user.setCurrentLoginIP(ip);
        user.setCurrentLoginDate(new Date());
        userRepository.save(user);
    }

}