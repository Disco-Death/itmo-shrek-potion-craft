package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.Ingredient;
import com.potion.ISPotion.Classes.Report;
import com.potion.ISPotion.Classes.Role;
import com.potion.ISPotion.Classes.User;
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

import java.util.*;

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

    @ModelAttribute("permissionsParts")
    public Set<String> headerPermission(@CurrentSecurityContext(expression="authentication")
                                        Authentication authentication) {
        return AuthUtils.getHeaderPermissions(userRepository, authentication);
    }

    @GetMapping("/report")
    public String report(@CurrentSecurityContext(expression="authentication")
                             Authentication authentication, Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.HEAD,
                Role.ADMIN
        ));
        Collection<Role> watcherRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN
        ));

        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!(AuthUtils.anyAllowedRole(userRoles, allowedRoles)))
            return "redirect:/home";

        User currentUser = AuthUtils.getUserByAuthentication(userRepository, authentication);
        ArrayList<Report> reports = new ArrayList<>();

        if (AuthUtils.anyAllowedRole(userRoles, watcherRoles)) {
            reports.addAll(reportRepository.findAllByIsSended(true));
            reports.addAll(reportRepository.findAllByUserAndIsSended(currentUser, false));
        }
        if (currentUser.getRoles().contains(Role.HEAD)) {
            reports.addAll(reportRepository.findAllByUser(currentUser));
        }

        model.addAttribute("reports", reports );
        model.addAttribute("title", "Отчеты");
        return "report";
    }
    @GetMapping("/report/{id}")
    public String reportDisplay(@CurrentSecurityContext(expression="authentication")
                                    Authentication authentication, @PathVariable(value = "id") long id, Model model) {
        Collection<Role> watcherRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.ADMIN
        ));

        User currentUser = AuthUtils.getUserByAuthentication(userRepository, authentication);
        Report report = reportRepository.findById(id).orElseThrow();

        if (!report.getUser().equals(currentUser) || !(AuthUtils.anyAllowedRole(currentUser.getRoles(), watcherRoles)))
            return "redirect:/report";

        model.addAttribute("report", report );
        model.addAttribute("title", "Отчеты");
        return "report-details";
    }
    @GetMapping("/report/add")
    public String reportAddDisplay(@CurrentSecurityContext(expression="authentication")
                                       Authentication authentication, Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.HEAD,
                Role.ADMIN
        ));
        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!(AuthUtils.anyAllowedRole(userRoles, allowedRoles)))
            return "redirect:/home";

        model.addAttribute("title", "Отчеты");
        return "report-add";
    }

    @PostMapping("/report/add")
    public String reportAdd(@CurrentSecurityContext(expression="authentication")
                                Authentication authentication, @RequestParam String title, @RequestParam String subject, @RequestParam String body, Model model) {
        Collection<Role> allowedRoles = new HashSet<>(Arrays.asList(
                Role.DIRECTOR,
                Role.HEAD,
                Role.ADMIN
        ));
        Collection<Role> userRoles = AuthUtils.getRolesByAuthentication(userRepository, authentication);
        if (!(AuthUtils.anyAllowedRole(userRoles, allowedRoles)))
            return "redirect:/home";

        User user = AuthUtils.getUserByAuthentication(userRepository, authentication);
        Report report = new Report(title, subject, body, user) ;
        reportRepository.save(report);
        return "redirect:/report";
    }

    @PostMapping("/report/send/{id}")
    public String ingredientDelete(@CurrentSecurityContext(expression="authentication")
                                       Authentication authentication, @PathVariable(value = "id") long id) {
        User currentUser = AuthUtils.getUserByAuthentication(userRepository, authentication);
        Report report = reportRepository.findById(id).orElseThrow();

        if (!report.getUser().equals(currentUser))
            return "redirect:/report";

        if (!reportRepository.existsById(id)) {
            return "redirect:/ingredient";
        }

        report.setSended(true);
        reportRepository.save(report);
        return "redirect:/report";
    }

    @GetMapping("/report/edit/{id}")
    public String reportEditDisplay(@CurrentSecurityContext(expression="authentication")
                                        Authentication authentication, @PathVariable(value = "id") long id, Model model) {
        if (!reportRepository.existsById(id)) {
            return "redirect:/report";
        }
        User currentUser = AuthUtils.getUserByAuthentication(userRepository, authentication);
        Report report = reportRepository.findById(id).orElseThrow();

        if (!report.getUser().equals(currentUser))
            return "redirect:/report";

        model.addAttribute("report", report );
        model.addAttribute("title", "report");
        return "report-edit";
    }
    @PostMapping("/report/edit/{id}")
    public String reportEdit(@CurrentSecurityContext(expression="authentication")
                                 Authentication authentication, @PathVariable(value = "id") long id, @RequestParam String title, @RequestParam String subject, @RequestParam String body, Model model) {
        if (!reportRepository.existsById(id)) {
            return "redirect:/report";
        }
        User currentUser = AuthUtils.getUserByAuthentication(userRepository, authentication);
        Report report = reportRepository.findById(id).orElseThrow();

        if (!report.getUser().equals(currentUser))
            return "redirect:/report";

        report.setTitle(title);
        report.setSubject(subject);
        report.setBody(body);
        report.setSended(false);
        reportRepository.save(report);
        return "redirect:/report";
    }
    @PostMapping("/report/delete/{id}")
    public String reportDelete(@CurrentSecurityContext(expression="authentication")
                                   Authentication authentication, @PathVariable(value = "id") long id) {
        if (!reportRepository.existsById(id)) {
            return "redirect:/report";
        }
        User currentUser = AuthUtils.getUserByAuthentication(userRepository, authentication);
        Report report = reportRepository.findById(id).orElseThrow();

        if (!report.getUser().equals(currentUser))
            return "redirect:/report";

        reportRepository.deleteById(id);
        return "redirect:/report";
    }
}
