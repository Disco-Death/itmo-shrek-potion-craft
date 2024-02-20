package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.*;
import com.potion.ISPotion.repo.*;
import com.potion.ISPotion.utils.AuthUtils;
import com.potion.ISPotion.utils.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Controller
public class TestsController {
    @Autowired
    private PotionRepository potionRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private StorageCellRepository storageCellRepository;
    @Autowired
    private StorageRecordRepository storageRecordRepository;
    @Autowired
    private StorageService storageService;
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
    @GetMapping("/tests")
    public String testsDisplay(@CurrentSecurityContext(expression="authentication")
                                   Authentication authentication,Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN,
                Role.HEAD,
                Role.TEST_DEPT
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        Iterable<StorageCell> cells = storageCellRepository.findAll();
        Iterable<StorageRecord> records = storageRecordRepository.findAll();

        model.addAttribute("cells", cells );
        model.addAttribute("records", records );
        model.addAttribute("title", "Тестирование");

        return "tests";
    }

    @PostMapping("/tests/edit/{id}")
    public String testEditStatus(@CurrentSecurityContext(expression="authentication")
                                     Authentication authentication,@PathVariable(value = "id") long id, Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN
        ));
        Collection<Role> allowedCombineRole = new HashSet<>(Arrays.asList(
                Role.HEAD,
                Role.TEST_DEPT
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles) || userRoles.containsAll(allowedCombineRole))
            return "redirect:/home";

        if (!storageCellRepository.existsById(id)) {
            return "redirect:/tests";
        }
        Iterable<StorageCell> cells = storageCellRepository.findAll();
        StorageCell currentCell = storageCellRepository.findById(id).orElseThrow();
        if (currentCell.getEntity() == StorageEntity.Potion) {
            currentCell.setTestApproved(1 - currentCell.getTestApproved());
            storageCellRepository.save(currentCell);
        }

        model.addAttribute("cells", cells );
        model.addAttribute("title", "Тестирование");
        return "redirect:/tests";
    }
}
