package com.potion.ISPotion.Controllers;
import com.potion.ISPotion.repo.UserRepository;
import com.potion.ISPotion.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Set;


@Controller
public class MainPageController {
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Зельевар");
        return "home";
    }

    @ModelAttribute("permissionsParts")
    public Set<String> headerPermission(@CurrentSecurityContext(expression="authentication")
                                        Authentication authentication) {
        return AuthUtils.getHeaderPermissions(userRepository, authentication);
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {return request.getRequestURI();
    }

}