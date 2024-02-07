package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.*;
import com.potion.ISPotion.repo.IngredientRepository;
import com.potion.ISPotion.repo.PotionRepository;
import com.potion.ISPotion.repo.StorageCellRepository;
import com.potion.ISPotion.repo.StorageRecordRepository;
import com.potion.ISPotion.utils.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

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

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }
    @GetMapping("/tests")
    public String testsDisplay(Model model) {
        Iterable<StorageCell> cells = storageCellRepository.findAll();
        Iterable<StorageRecord> records = storageRecordRepository.findAll();

        model.addAttribute("cells", cells );
        model.addAttribute("records", records );
        model.addAttribute("title", "Tests");

        return "tests";
    }

    @PostMapping("/tests/edit/{id}")
    public String testEditStatus(@PathVariable(value = "id") long id, Model model) {
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
        model.addAttribute("title", "Tests");
        return "redirect:/tests";
    }
}
