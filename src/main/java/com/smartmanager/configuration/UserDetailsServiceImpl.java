package com.smartmanager.configuration;

import com.smartmanager.models.User;
import com.smartmanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserDetailsServiceImpl implements UserDetailsService {

//    Fetching User from database

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.getUserByUserName(username);

        if (user == null) {
            throw new UsernameNotFoundException("Could not found User!!!! ");
        }

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        return customUserDetails;
    }
}
