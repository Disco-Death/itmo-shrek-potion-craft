package com.potion.ISPotion.utils;

import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;

import java.util.*;

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
        return user != null ? user.getRoles() : new HashSet<Role>();
    }

    public static boolean allAllowedRole(Collection<Role> userRoles, Collection<Role> validRoles) {
        Set<Role> uR = new HashSet<Role>(userRoles);
        Set<Role> vR = new HashSet<Role>(validRoles);
        return vR.containsAll(uR);
    }

    public static Set<String> getHeaderPermissions(UserRepository userRepository, Authentication authentication) {
        if (authentication.getPrincipal() == "anonymousUser") {
            ArrayList<String> permissionsArray = new ArrayList<>();
            permissionsArray.add("Anonymously");
            return new HashSet<>(permissionsArray);
        }
        Set<Role> userRoles = getRolesByAuthentication(userRepository, authentication);
        ArrayList<String> permissionsArray = new ArrayList<>();

        if (userRoles.contains(Role.ADMIN) || userRoles.contains(Role.DIRECTOR) || userRoles.contains(Role.MERLIN)) {
            permissionsArray.addAll(Arrays.asList("ingredient","potion","report","sale","storage","tests","users","stats","tasks","track"));
        }
        if (userRoles.contains(Role.TEST_DEPT)) {
            permissionsArray.addAll(Arrays.asList("storage","tests","tasks"));
        }
        if (userRoles.contains(Role.HEAD)) {
            permissionsArray.addAll(Arrays.asList("storage","report","tasks"));
        }
        if (userRoles.contains(Role.PICKING_DEPT)) {
            permissionsArray.addAll(Arrays.asList("storage","ingredient","tasks"));
        }
        if (userRoles.contains(Role.POTIONS_MAKING_DEPT)) {
            permissionsArray.addAll(Arrays.asList("storage","potion", "ingredient","tasks"));
        }
        if (userRoles.contains(Role.SALES_DEPT)) {
            permissionsArray.addAll(Arrays.asList("storage","sale","tasks"));
        }
        if (anyAllowedRole(userRoles, new HashSet<>(Arrays.asList(Role.EMPLOYEE, Role.HEAD)))) {
            permissionsArray.addAll(Arrays.asList("storage","tasks"));
        }

        HashSet<String> permissions = new HashSet<>(permissionsArray);

        return permissions;
    }
}
