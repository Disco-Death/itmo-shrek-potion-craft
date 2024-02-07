package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.Ingredient;
import com.potion.ISPotion.Classes.Report;
import com.potion.ISPotion.repo.ReportRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ReportController {
    @Autowired
    private ReportRepository reportRepository;

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }
    @GetMapping("/report")
    public String report(Model model) {
        Iterable<Report> reports = reportRepository.findAll();
        model.addAttribute("reports", reports );
        model.addAttribute("title", "reports");
        return "report";
    }
    @GetMapping("/report/{id}")
    public String reportDisplay(@PathVariable(value = "id") long id, Model model) {
        Report report = reportRepository.findById(id).orElseThrow();
        model.addAttribute("report", report );
        model.addAttribute("title", "reports");
        return "report-details";
    }
    @GetMapping("/report/add")
    public String reportAddDisplay(Model model) {
        return "report-add";
    }

    @PostMapping("/report/add")
    public String reportAdd(@RequestParam String title, @RequestParam String subject, @RequestParam String body, Model model) {
        Report report = new Report(title, subject, body) ;
        reportRepository.save(report);
        return "redirect:/report";
    }

    @PostMapping("/report/send/{id}")
    public String ingredientDelete(@PathVariable(value = "id") long id) {
        if (!reportRepository.existsById(id)) {
            return "redirect:/ingredient";
        }
        Report report = reportRepository.findById(id).orElseThrow();
        report.setSended(true);
        reportRepository.save(report);
        return "redirect:/report";
    }

    @GetMapping("/report/edit/{id}")
    public String reportEditDisplay(@PathVariable(value = "id") long id, Model model) {
        if (!reportRepository.existsById(id)) {
            return "redirect:/report";
        }
        Report report = reportRepository.findById(id).orElseThrow();
        model.addAttribute("report", report );
        model.addAttribute("title", "report");
        return "report-edit";
    }
    @PostMapping("/report/edit/{id}")
    public String reportEdit(@PathVariable(value = "id") long id, @RequestParam String title, @RequestParam String subject, @RequestParam String body, Model model) {
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
    public String reportDelete(@PathVariable(value = "id") long id) {
        if (!reportRepository.existsById(id)) {
            return "redirect:/report";
        }
        reportRepository.deleteById(id);
        return "redirect:/report";
    }
}
