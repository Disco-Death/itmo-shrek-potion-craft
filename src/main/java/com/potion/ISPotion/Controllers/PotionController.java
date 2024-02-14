package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.Ingredient;
import com.potion.ISPotion.Classes.Potion;
import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.StorageCell;
import com.potion.ISPotion.repo.IngredientRepository;
import com.potion.ISPotion.repo.PotionRepository;
import com.potion.ISPotion.repo.UserRepository;
import com.potion.ISPotion.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class PotionController {
    @Autowired
    private PotionRepository potionRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private UserRepository userRepository;

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("permissionsParts")
    public Set<String> headerPermission(@CurrentSecurityContext(expression="authentication")
                                        Authentication authentication) {
        return AuthUtils.getHeaderPermissions(userRepository, authentication);
    }

    @GetMapping("/potion/add")
    public String potionDisplayAdd(@CurrentSecurityContext(expression="authentication")
                                       Authentication authentication, Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN
        ));
        Collection<Role> allowedCombineRole = new HashSet<>(Arrays.asList(
                Role.HEAD,
                Role.POTIONS_MAKING_DEPT
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles) || userRoles.containsAll(allowedCombineRole))
            return "redirect:/home";

        Iterable<Ingredient> ingredients = ingredientRepository.findAll();
        model.addAttribute("ingredients", ingredients );
        model.addAttribute("title", "Зелья");
        return "potion-add";
    }


    @GetMapping("/potion")
    public String potion(@CurrentSecurityContext(expression="authentication")
                             Authentication authentication, Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.HEAD,
                Role.POTIONS_MAKING_DEPT,
                Role.DIRECTOR,
                Role.ADMIN
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";
        Iterable<Potion> potions = potionRepository.findAll();
        model.addAttribute("potions", potions );
        model.addAttribute("title", "Зелья");
        return "potion";
    }
    @GetMapping("/potion/edit/{id}")
    public String potionEditDisplay(@CurrentSecurityContext(expression="authentication")
                                        Authentication authentication, @PathVariable(value = "id") long id, Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN
        ));
        Collection<Role> allowedCombineRole = new HashSet<>(Arrays.asList(
                Role.HEAD,
                Role.POTIONS_MAKING_DEPT
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles) || userRoles.containsAll(allowedCombineRole))
            return "redirect:/home";

        if (!potionRepository.existsById(id)) {
            return "redirect:/potion";
        }
        Potion potion = potionRepository.findById(id).orElseThrow();
        Iterable<Ingredient> ingredients = ingredientRepository.findAll();

        model.addAttribute("ingredients", ingredients );
        model.addAttribute("potion", potion );
        model.addAttribute("title", "Зелья");
        return "potion-edit";
    }
    @PostMapping("/potion/edit/{id}")
    public String potionEdit(@CurrentSecurityContext(expression="authentication")
                                 Authentication authentication, @PathVariable(value = "id") long id, HttpServletRequest request, Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN
        ));
        Collection<Role> allowedCombineRole = new HashSet<>(Arrays.asList(
                Role.HEAD,
                Role.POTIONS_MAKING_DEPT
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles) || userRoles.containsAll(allowedCombineRole))
            return "redirect:/home";

        if (!potionRepository.existsById(id)) {
            return "redirect:/potion";
        }
        String[] ingredientsIds = request.getParameterValues("ingredientsIds");
        String name = Arrays.toString(request.getParameterValues("name")).replace("[", "").replace("]", "");

        ArrayList<Long> newIngredientIds = new ArrayList<Long>();
        ArrayList<Ingredient> newIngredients = new ArrayList<Ingredient>();
        for (String ingredientsId : ingredientsIds) {
            newIngredientIds.add(Long.parseLong(ingredientsId));
            newIngredients.add(ingredientRepository.findById(Long.parseLong(ingredientsId)).orElseThrow());
        }
        Potion potion = potionRepository.findById(id).orElseThrow();
        potion.setName(name);
        potion.setIngredients(newIngredients);
        potion.setIngredientsIds(newIngredientIds);

        potionRepository.save(potion);
        return "redirect:/potion";
    }

    @PostMapping("/potion/add")
    public String potionAdd(@CurrentSecurityContext(expression="authentication")
                                Authentication authentication, HttpServletRequest request, Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN
        ));
        Collection<Role> allowedCombineRole = new HashSet<>(Arrays.asList(
                Role.HEAD,
                Role.POTIONS_MAKING_DEPT
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles) || userRoles.containsAll(allowedCombineRole))
            return "redirect:/home";

        String[] ingredientsIds = request.getParameterValues("ingredientsIds");
        String name = Arrays.toString(request.getParameterValues("name")).replace("[", "").replace("]", "");

        ArrayList<Long> ids = new ArrayList<Long>();
        ArrayList<Ingredient> ingredients = new ArrayList<Ingredient>();
        for (String ingredientId: ingredientsIds) {
            ids.add(Long.parseLong(ingredientId));
            ingredients.add(ingredientRepository.findById(Long.parseLong(ingredientId)).orElseThrow());
        }
        Potion potion = new Potion(name, ids, ingredients) ;
        potionRepository.save(potion);
        return "redirect:/potion";
    }
    @PostMapping("/potion/delete/{id}")
    public String potionDelete(@CurrentSecurityContext(expression="authentication")
                                   Authentication authentication, @PathVariable(value = "id") long id) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN
        ));
        Collection<Role> allowedCombineRole = new HashSet<>(Arrays.asList(
                Role.HEAD,
                Role.POTIONS_MAKING_DEPT
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles) || userRoles.containsAll(allowedCombineRole))
            return "redirect:/home";

        if (!potionRepository.existsById(id)) {
            return "redirect:/potion";
        }
        potionRepository.deleteById(id);
        return "redirect:/potion";
    }
}
