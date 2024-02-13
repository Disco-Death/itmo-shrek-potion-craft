package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.Potion;
import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.Sale;
import com.potion.ISPotion.repo.PotionRepository;
import com.potion.ISPotion.repo.SaleRepository;
import com.potion.ISPotion.repo.UserRepository;
import com.potion.ISPotion.utils.AuthUtils;
import com.potion.ISPotion.utils.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

@Controller
public class SaleController {
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private PotionRepository potionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StorageService storageService;

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/sale")
    public String sale(@CurrentSecurityContext(expression="authentication")
                           Authentication authentication,
                       Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.SALES_DEPT,
                Role.HEAD,
                Role.MERLIN
        ));
        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        Iterable<Sale> sales = saleRepository.findAll();

        model.addAttribute("title", "Продажи");
        model.addAttribute("sales", sales);

        return "sale";
    }

    @GetMapping("/sale/{id}")
    public String saleDisplay(@CurrentSecurityContext(expression="authentication")
                                  Authentication authentication,
                              @PathVariable(value = "id") long id,
                              Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.SALES_DEPT,
                Role.HEAD,
                Role.MERLIN
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        Sale sale = saleRepository.findById(id).orElseThrow();

        model.addAttribute("title", "Продажа");
        model.addAttribute("sale", sale);

        return "sale-details";
    }

    @GetMapping("/sale/add")
    public String saleAddDisplay(@CurrentSecurityContext(expression="authentication")
                                     Authentication authentication,
                                 Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.SALES_DEPT,
                Role.HEAD,
                Role.MERLIN
        ));

        var userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!(AuthUtils.anyAllowedRole(userRoles, allowedRoles)))
            return "redirect:/home";

        var potionsIds = storageService.getAllPotionsIdsForSale();
        var potions = potionRepository.findAllById(potionsIds);

        model.addAttribute("title", "Добавить продажу");
        model.addAttribute("potions", potions);

        return "sale-add";
    }

    @PostMapping("/sale/add")
    public String saleAdd(@CurrentSecurityContext(expression="authentication")
                              Authentication authentication,
                          @RequestParam Long potionId,
                          @RequestParam Long quantity,
                          @RequestParam Long price,
                          @RequestParam String client,
                          Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.SALES_DEPT,
                Role.HEAD,
                Role.MERLIN
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!(AuthUtils.anyAllowedRole(userRoles, allowedRoles)))
            return "redirect:/home";

        if (!potionRepository.existsById(potionId))
            return "redirect:/sale";

        boolean successTaking = storageService.takePotionsFromStorageForSaleByPotionId(potionId, quantity);

        if (!successTaking)
            return "redirect:/storage";

        Potion potion = potionRepository.findById(potionId).orElseThrow();
        Sale sale = new Sale(potion, quantity, price, client);
        saleRepository.save(sale);

        return "redirect:/sale";
    }

    @GetMapping("/sale/edit/{id}")
    public String saleEditDisplay(@CurrentSecurityContext(expression="authentication")
                                      Authentication authentication,
                                  @PathVariable(value = "id") long id,
                                  Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.HEAD,
                Role.MERLIN
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!(AuthUtils.anyAllowedRole(userRoles, allowedRoles)))
            return "redirect:/home";

        if (!saleRepository.existsById(id))
            return "redirect:/sale";

        Sale sale = saleRepository.findById(id).orElseThrow();
        Iterable<Potion> potions = potionRepository.findAll();

        model.addAttribute("title", "Изменить продажу");
        model.addAttribute("sale", sale );
        model.addAttribute("potions", potions);

        return "sale-edit";
    }

    @PostMapping("/sale/edit/{id}")
    public String saleEdit(@CurrentSecurityContext(expression="authentication")
                               Authentication authentication,
                           @PathVariable(value = "id") long id,
                           @RequestParam Long potionId,
                           @RequestParam Long quantity,
                           @RequestParam Long price,
                           @RequestParam String client,
                           Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.HEAD,
                Role.MERLIN
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!(AuthUtils.anyAllowedRole(userRoles, allowedRoles)))
            return "redirect:/home";

        if (!saleRepository.existsById(id))
            return "redirect:/sale";
        if (!potionRepository.existsById(potionId))
            return "redirect:/sale";

        Sale sale = saleRepository.findById(id).orElseThrow();
        Potion salePotion = sale.getPotion();

        if (potionId.equals(salePotion.getId())) {
            if (quantity < sale.getQuantity()) {
                storageService.returnPotionsToStorageForSaleByPotionId(potionId, sale.getQuantity() - quantity);
            } else if (quantity > sale.getQuantity()) {
                boolean successTaking = storageService.takePotionsFromStorageForSaleByPotionId(potionId, quantity - sale.getQuantity());

                if (!successTaking) {
                    return "redirect:/sale";
                }
            }
        } else {
            boolean successTaking = storageService.takePotionsFromStorageForSaleByPotionId(potionId, quantity);

            if (!successTaking) {
                return "redirect:/sale";
            }

            storageService.returnPotionsToStorageForSaleByPotionId(salePotion.getId(), sale.getQuantity());

            Potion potion = potionRepository.findById(potionId).orElseThrow();
            sale.setPotion(potion);
        }

        sale.setQuantity(quantity);
        sale.setPrice(price);
        sale.setClient(client);
        saleRepository.save(sale);

        return "redirect:/sale";
    }

    @PostMapping("/sale/delete/{id}")
    public String saleDelete(@CurrentSecurityContext(expression="authentication")
                                 Authentication authentication,
                             @PathVariable(value = "id") long id) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.HEAD,
                Role.MERLIN
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!(AuthUtils.anyAllowedRole(userRoles, allowedRoles)))
            return "redirect:/home";

        if (!saleRepository.existsById(id))
            return "redirect:/sale";

        Sale sale = saleRepository.findById(id).orElseThrow();
        Potion salePotion = sale.getPotion();

        storageService.returnPotionsToStorageForSaleByPotionId(salePotion.getId(), sale.getQuantity());

        saleRepository.deleteById(id);

        return "redirect:/sale";
    }
}
