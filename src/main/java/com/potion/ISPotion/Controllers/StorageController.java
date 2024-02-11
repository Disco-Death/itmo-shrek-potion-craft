package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.*;
import com.potion.ISPotion.repo.IngredientRepository;
import com.potion.ISPotion.repo.PotionRepository;
import com.potion.ISPotion.repo.StorageCellRepository;
import com.potion.ISPotion.repo.StorageRecordRepository;
import com.potion.ISPotion.utils.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;


@Controller
public class StorageController {
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

    @GetMapping("/storage")
    public String storage(Model model) {
        Iterable<StorageCell> cells = storageCellRepository.findAll();
        Iterable<StorageRecord> records = storageRecordRepository.findAll();

        model.addAttribute("cells", cells );
        model.addAttribute("records", records );
        model.addAttribute("title", "Склад");

        return "storage";
    }

    @GetMapping("/storage/add")
    public String storageDisplayAdd(Model model) {
        Iterable<Ingredient> ingredients = ingredientRepository.findAll();
        Iterable<Potion> potions = potionRepository.findAll();

        model.addAttribute("ingredients", ingredients );
        model.addAttribute("potions", potions );
        model.addAttribute("entities", StorageEntity.values());
        model.addAttribute("title", "Склад");
        return "storage-cell-add";
    }

    @PostMapping("/storage/add")
    public String storageCellPostAdd(@RequestParam String entity, @RequestParam String ingredientId, @RequestParam String potionId, @RequestParam String quantity, Model model) {
        try {
            StorageCell cell = new StorageCell();
            switch(entity) {
                case ("Ingredient"):
                    cell.setEntity(StorageEntity.Ingredient);
                    cell.setEntity_id(Long.parseLong(ingredientId));
                    cell.setQuantity(Long.parseLong(quantity));
                    break;
                case ("Potion"):
                    cell.setEntity(StorageEntity.Potion);
                    cell.setEntity_id(Long.parseLong(potionId));
                    cell.setQuantity(Long.parseLong(quantity));
                    break;
                default:
                    return "redirect:/storage";
            }
            storageService.storageCellCreate(cell);
        } catch (IllegalArgumentException e) {
        }
        return "redirect:/storage";
    }
    @GetMapping("/storage/edit/{id}")
    public String storageCellEditDisplay(@PathVariable(value = "id") long id, Model model) {
        if (!storageCellRepository.existsById(id)) {
            return "redirect:/storage";
        }
        Iterable<Ingredient> ingredients = ingredientRepository.findAll();
        Iterable<Potion> potions = potionRepository.findAll();
        StorageCell currentCell = storageCellRepository.findById(id).orElseThrow();

        model.addAttribute("ingredients", ingredients );
        model.addAttribute("potions", potions );
        model.addAttribute("entities", StorageEntity.values());
        model.addAttribute("currentCell", currentCell );
        model.addAttribute("title", "Склад");
        return "storage-cell-edit";
    }
    @PostMapping("/storage/edit/{id}")
    public String storageCellEdit(@PathVariable(value = "id") long id, @RequestParam String quantity, Model model) {
        if (!storageCellRepository.existsById(id)) {
            return "redirect:/storage";
        }
        StorageCell cell = storageCellRepository.findById(id).orElseThrow();
        storageService.storageCellUpdate(cell, Long.parseLong(quantity));

        return "redirect:/storage";
    }

    @PostMapping("/storage/delete/{id}")
    public String storageCellDelete(@PathVariable(value = "id") long id) {
        if (!storageCellRepository.existsById(id)) {
            return "redirect:/storage";
        }
        StorageCell cell = storageCellRepository.findById(id).orElseThrow();
        storageService.storageCellRemove(cell);
        return "redirect:/storage";
    }

    @PostMapping("/storage/record/restore/{id}")
    public String recordDelete(@PathVariable(value = "id") long id) {
        if (!storageRecordRepository.existsById(id)) {
            return "redirect:/storage";
        }
        StorageRecord record = storageRecordRepository.findById(id).orElseThrow();
        storageService.storageRecordRestoreOperation(record);
        return "redirect:/storage";
    }

}
