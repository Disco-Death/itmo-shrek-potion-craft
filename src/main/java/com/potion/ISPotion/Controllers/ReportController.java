package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.Ingredient;
import com.potion.ISPotion.Classes.Report;
import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.repo.ReportRepository;
import com.potion.ISPotion.repo.UserRepository;
import com.potion.ISPotion.utils.AuthUtils;
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
public class ReportController {
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private UserRepository userRepository;

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping("/report")
    public String report(@CurrentSecurityContext(expression="authentication")
                             Authentication authentication,
                         Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.EMPLOYEE,
                Role.HEAD,
                Role.MERLIN
        ));
        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        Iterable<Report> reports = reportRepository.findAll();
        model.addAttribute("reports", reports );
        model.addAttribute("title", "Отчеты");
        return "report";
    }

    @GetMapping("/report/{id}")
    public String reportDisplay(@CurrentSecurityContext(expression="authentication")
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

        Report report = reportRepository.findById(id).orElseThrow();
        model.addAttribute("report", report );
        model.addAttribute("title", "Отчеты");
        return "report-details";
    }

    @GetMapping("/report/add")
    public String reportAddDisplay(@CurrentSecurityContext(expression="authentication")
                                       Authentication authentication,
                                   Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.EMPLOYEE,
                Role.HEAD,
                Role.MERLIN
        ));
        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        model.addAttribute("title", "Отчеты");
        return "report-add";
    }

    @PostMapping("/report/add")
    public String reportAdd(@CurrentSecurityContext(expression="authentication")
                                Authentication authentication,
                            @RequestParam String title,
                            @RequestParam String subject,
                            @RequestParam String body,
                            Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.EMPLOYEE,
                Role.HEAD,
                Role.MERLIN
        ));
        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        Report report = new Report(title, subject, body) ;
        reportRepository.save(report);
        return "redirect:/report";
    }

    @PostMapping("/report/send/{id}")
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

        if (!reportRepository.existsById(id)) {
            return "redirect:/ingredient";
        }
        Report report = reportRepository.findById(id).orElseThrow();
        report.setSended(true);
        reportRepository.save(report);
        return "redirect:/report";
    }

    @GetMapping("/report/edit/{id}")
    public String reportEditDisplay(@CurrentSecurityContext(expression="authentication")
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

        if (!reportRepository.existsById(id)) {
            return "redirect:/report";
        }
        Report report = reportRepository.findById(id).orElseThrow();
        model.addAttribute("report", report );
        model.addAttribute("title", "report");
        return "report-edit";
    }

    @PostMapping("/report/edit/{id}")
    public String reportEdit(@CurrentSecurityContext(expression="authentication")
                                 Authentication authentication,
                             @PathVariable(value = "id") long id,
                             @RequestParam String title,
                             @RequestParam String subject,
                             @RequestParam String body,
                             Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.ADMIN,
                Role.HEAD,
                Role.MERLIN
        ));
        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!AuthUtils.anyAllowedRole(userRoles, allowedRoles))
            return "redirect:/home";

        if (!reportRepository.existsById(id)) {
            return "redirect:/report";
        }
        Report report = reportRepository.findById(id).orElseThrow();
        report.setTitle(title);
        report.setSubject(subject);
        report.setBody(body);
        report.setSended(false);
        reportRepository.save(report);
        return "redirect:/report";
    }

    @PostMapping("/report/delete/{id}")
    public String reportDelete(@CurrentSecurityContext(expression="authentication")
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

        if (!reportRepository.existsById(id)) {
            return "redirect:/report";
        }
        reportRepository.deleteById(id);
        return "redirect:/report";
    }
}
