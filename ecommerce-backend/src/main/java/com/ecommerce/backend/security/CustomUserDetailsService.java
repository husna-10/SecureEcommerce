package com.ecommerce.backend.security;

import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username: {}", username);
        
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> {
                    log.warn("User not found with username or email: {}", username);
                    return new UsernameNotFoundException("User not found with username or email: " + username);
                });

        log.debug("User found: {}, Enabled: {}, Account Non Locked: {}", 
                 user.getUsername(), user.isEnabled(), user.isAccountNonLocked());

        return user;
    }

    @Transactional(readOnly = true)
    public User loadUserEntityByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user entity by username: {}", username);
        
        return userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> {
                    log.warn("User entity not found with username or email: {}", username);
                    return new UsernameNotFoundException("User not found with username or email: " + username);
                });
    }
}