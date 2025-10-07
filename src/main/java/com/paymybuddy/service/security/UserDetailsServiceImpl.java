package com.paymybuddy.service.security;

import com.paymybuddy.model.User;
import com.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        } else {
            User user = optionalUser.get();
            return org.springframework.security.core.userdetails.User.builder() //Use a Spring Security User object
                    .username(user.getEmail()) //Username is email for our application
                    .password(user.getPassword())
                    .roles("USER") // Set the default role to USER.
                    .build();
        }
    }
}
