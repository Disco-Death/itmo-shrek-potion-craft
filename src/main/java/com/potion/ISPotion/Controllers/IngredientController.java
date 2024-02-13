package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.Ingredient;
import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.repo.UserRepository;
import com.potion.ISPotion.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.potion.ISPotion.repo.IngredientRepository;

import java.util.*;

@Controller
public class IngredientController {
    @Autowired
    public IngredientRepository ingredientRepository;
    @Autowired
    private UserRepository userRepository;

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/ingredient/add")
    public String ingredientDisplayAdd(@CurrentSecurityContext(expression="authentication")
                                           Authentication authentication,
                                       Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.PICKING_DEPT,
                Role.HEAD,
                Role.MERLIN
        ));
        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        model.addAttribute("title", "Ингредиенты");
        return "ingredient-add";
    }


    @GetMapping("/ingredient")
    public String ingredient(@CurrentSecurityContext(expression="authentication")
                                 Authentication authentication,
                             Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.PICKING_DEPT,
                Role.HEAD,
                Role.MERLIN
        ));
        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        Iterable<Ingredient> ingredients = ingredientRepository.findAll();
        model.addAttribute("ingredients", ingredients );
        model.addAttribute("title", "Ингредиенты");
        return "ingredient";
    }

    @GetMapping("/ingredient/edit/{id}")
    public String ingredientEditDisplay(@CurrentSecurityContext(expression="authentication")
                                            Authentication authentication,
                                        @PathVariable(value = "id") long id,
                                        Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.HEAD,
                Role.MERLIN
        ));
        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        if (!ingredientRepository.existsById(id)) {
            return "redirect:/ingredient";
        }
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow();
        model.addAttribute("ingredient", ingredient );
        model.addAttribute("title", "Ингредиенты");
        return "ingredient-edit";
    }

    @PostMapping("/ingredient/edit/{id}")
    public String ingredientEdit(@CurrentSecurityContext(expression="authentication")
                                     Authentication authentication,
                                 @PathVariable(value = "id") long id,
                                 @RequestParam String name,
                                 @RequestParam String property,
                                 Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.HEAD,
                Role.MERLIN
        ));
        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        if (!ingredientRepository.existsById(id)) {
            return "redirect:/ingredient";
        }
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow();
        ingredient.setName(name);
        ingredient.setProperty(property);
        ingredientRepository.save(ingredient);
        return "redirect:/ingredient";
    }

    @PostMapping("/ingredient/add")
    public String ingredientAdd(@CurrentSecurityContext(expression="authentication")
                                    Authentication authentication,
                                @RequestParam String name,
                                @RequestParam String property,
                                Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.PICKING_DEPT,
                Role.HEAD,
                Role.MERLIN
        ));
        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        Ingredient ingredient = new Ingredient(name, property) ;
        ingredientRepository.save(ingredient);
        return "redirect:/ingredient";
    }

    @PostMapping("/ingredient/delete/{id}")
    public String ingredientDelete(@CurrentSecurityContext(expression="authentication")
                                       Authentication authentication,
                                   @PathVariable(value = "id") long id) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.HEAD,
                Role.MERLIN
        ));
        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        if (!ingredientRepository.existsById(id)) {
            return "redirect:/ingredient";
        }
        ingredientRepository.deleteById(id);
        return "redirect:/ingredient";
    }
}
