package com.potion.ISPotion.utils;

import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Set;

public final class AuthUtils {
    private AuthUtils(){}

    public static boolean anyAllowedRole(Collection<Role> userRoles, Collection<Role> validRoles) {
        return CollectionUtils.containsAny(userRoles, validRoles);
    }

    public static User getUserByAuthentication(UserRepository userRepository, Authentication authentication) {
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    public static Set<Role> getRolesByAuthentication(UserRepository userRepository, Authentication authentication) {
        User user = getUserByAuthentication(userRepository, authentication);
        return user.getRoles();
    }
}
