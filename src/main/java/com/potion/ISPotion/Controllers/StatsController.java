package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.repo.UserRepository;
import com.potion.ISPotion.utils.AuthUtils;
import com.potion.ISPotion.utils.StatsComponent;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

@Controller
public class StatsController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StatsComponent statsComponent;

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/stats")
    public String stats(@CurrentSecurityContext(expression="authentication")
                       Authentication authentication,
                       Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.SALES_DEPT,
                Role.HEAD,
                Role.DIRECTOR,
                Role.EMPLOYEE
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        var stats = statsComponent.getStats();

        model.addAttribute("title", "Характер роста ИС");
        model.addAttribute("stats", stats);

        return "stats";
    }

}
