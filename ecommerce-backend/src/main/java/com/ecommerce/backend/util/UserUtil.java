package com.ecommerce.backend.util;

import com.ecommerce.backend.entity.User;
import com.ecommerce.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtil {

    private static UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        UserUtil.userRepository = userRepository;
    }

    public static Long getCurrentUserId(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            // Since our UserDetailsService returns User entity, we can cast it
            if (userDetails instanceof User) {
                User user = (User) userDetails;
                return user.getId();
            } else {
                // Fallback: look up user by username
                String username = userDetails.getUsername();
                User user = userRepository.findByUsernameOrEmail(username, username)
                    .orElseThrow(() -> new IllegalStateException("User not found: " + username));
                return user.getId();
            }
        }
        throw new IllegalStateException("No authenticated user found");
    }

    public static String getCurrentUsername(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userDetails.getUsername();
        }
        throw new IllegalStateException("No authenticated user found");
    }

    public static User getCurrentUser(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails instanceof User) {
                return (User) userDetails;
            } else {
                // Fallback: look up user by username
                String username = userDetails.getUsername();
                return userRepository.findByUsernameOrEmail(username, username)
                    .orElseThrow(() -> new IllegalStateException("User not found: " + username));
            }
        }
        throw new IllegalStateException("No authenticated user found");
    }
}
