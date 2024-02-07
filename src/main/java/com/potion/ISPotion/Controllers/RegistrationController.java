package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.*;
import com.potion.ISPotion.repo.UserRepository;
import com.potion.ISPotion.utils.AuthUtils;
import com.potion.ISPotion.utils.WebCamService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.io.IOException;
import java.util.*;


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
        user.setRoles(Collections.singleton(Role.USER));
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
        ArrayList<Role> newUserRoles = new ArrayList<Role>();
        String[] roles = request.getParameterValues("roles");
        String newPassword = Arrays.toString(request.getParameterValues("password")).replace("[", "").replace("]", "");
        Boolean newIsActive = Arrays.toString(request.getParameterValues("isActive")).contains("on");
        for (String role : roles
        ) {
            newUserRoles.add(Role.valueOf(role));
        }
        Set<Role> newRoles = new HashSet<Role>(newUserRoles);

        User user = userRepository.findById(id).orElseThrow();
        if (!newPassword.isEmpty() && !user.getPassword().contentEquals(newPassword)) {
            user.setPassword(newPassword);
        }
        if (!newRoles.isEmpty() && !user.getRoles().containsAll(newRoles)) {
            user.setRoles(newRoles);
        }
        if (user.isActive() != newIsActive) {
            user.setActive(newIsActive);
        }

        userRepository.save(user);

        return "redirect:/users";
    }

    @GetMapping("/users/add")
    public String userDisplayAdd(Model model) {
        model.addAttribute("roles", Role.values() );
        model.addAttribute("title", "Добавление нового пользователя");
        return "user-add";
    }

    @PostMapping("/users/add")
    public String userAdd(HttpServletRequest request, Model model) {
        ArrayList<Role> newUserRoles = new ArrayList<Role>();
        String[] roles = request.getParameterValues("roles");
        String newUserName = Arrays.toString(request.getParameterValues("user-name")).replace("[", "").replace("]", "");
        String newPassword = Arrays.toString(request.getParameterValues("password")).replace("[", "").replace("]", "");
        Boolean newIsActive = Arrays.toString(request.getParameterValues("isActive")).contains("on");
        for (String role : roles
        ) {
            newUserRoles.add(Role.valueOf(role));
        }
        Set<Role> newRoles = new HashSet<Role>(newUserRoles);

        User user = new User();

        if (newRoles.isEmpty()) {
            newRoles = Collections.singleton(Role.USER);
        }
        user.setUsername(newUserName);
        user.setPassword(newPassword);
        user.setRoles(newRoles);
        user.setActive(newIsActive);

        userRepository.save(user);
        return "redirect:/users";
    }
    @PostMapping("/users/delete/{id}")
    public String userDelete(@PathVariable(value = "id") long id) {
        if (!userRepository.existsById(id)) {
            return "redirect:/users";
        }
        userRepository.deleteById(id);
        return "redirect:/users";
    }
}
