package com.scaler.userservice.security.service;

import com.scaler.userservice.models.User;
import com.scaler.userservice.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    UserRepository userRepository;

    public CustomUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
           Optional<User> userOptional= userRepository.findByEmail(email);
           if(userOptional.isEmpty()) {
               throw new UsernameNotFoundException("user not found ");
           }
           User user = userOptional.get();
           return new CustomUserDetails(user);

    }
}
