package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.*;
import com.potion.ISPotion.repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.Collections;


@Controller
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }
    @GetMapping("/home")
    public  String home(@CurrentSecurityContext(expression="authentication")
                            Authentication authentication,  Model model) throws IOException {

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
        user.setRoles(Collections.singleton(Role.EMPLOYEE));
        userRepository.save(user);
        return "redirect:/login";
    }
    @GetMapping("/users")
    public  String usersDisplay(Model model) {
        Iterable<User> users = userRepository.findAll();

        model.addAttribute("users", users );
        model.addAttribute("title", "Пользователи");
        return "users";
    }

    @GetMapping("/users/edit/{id}")
    public String userEditDisplay(@PathVariable(value = "id") long id, Model model) {
        if (!userRepository.existsById(id)) {
            return "redirect:/users";
        }
        User user = userRepository.findById(id).orElseThrow();
        user.getRoles().contains(Role.EMPLOYEE);
        model.addAttribute("user", user );
        model.addAttribute("roles", Role.values() );

        model.addAttribute("title", "Редактирование пользователя");
        return "user-edit";
    }

    @PostMapping("/users/edit/{id}")
    public String userEdit(@PathVariable(value = "id") long id, HttpServletRequest request, Model model) {
        if (!userRepository.existsById(id)) {
            return "redirect:/users";
        }
        //request.getParameterValues("roles")
        User user = userRepository.findById(id).orElseThrow();

        return "redirect:/users";
    }
}
