package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.User;
import com.potion.ISPotion.repo.UserRepository;
import com.potion.ISPotion.utils.AuthUtils;
import com.potion.ISPotion.utils.WebCamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.Collections;


@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    WebCamService webCamService;
    @GetMapping("/home")
    public  String home(@CurrentSecurityContext(expression="authentication")
                            Authentication authentication,  Model model) throws IOException {
        User user = AuthUtils.getUserByAuthentication(userRepository, authentication);
        webCamService.snapshot(user.getUsername());
        return "home";
    }
    @GetMapping("/registration")
    public  String registration(Model model) {
        return "registration";
    }

    @PostMapping("/registration")
    public  String addUser(User user, Model model) {
        User userFromDB = userRepository.findByUsername(user.getUsername());
        if (userFromDB != null) {
            model.addAttribute("message", "User already exist");
            return "registration";
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);
        return "redirect:/login";
    }
}
