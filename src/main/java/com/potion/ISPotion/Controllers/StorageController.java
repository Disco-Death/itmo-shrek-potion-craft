package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.*;
import com.potion.ISPotion.repo.IngredientRepository;
import com.potion.ISPotion.repo.PotionRepository;
import com.potion.ISPotion.repo.StorageCellRepository;
import com.potion.ISPotion.repo.StorageRecordRepository;
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

    @GetMapping("/storage")
    public String storage(Model model) {
        Iterable<StorageCell> cells = storageCellRepository.findAll();
        model.addAttribute("cells", cells );
        model.addAttribute("title", "Storage");
        return "storage";
    }

    @GetMapping("/storage/add")
    public String storageDisplayAdd(Model model) {
        Iterable<Ingredient> ingredients = ingredientRepository.findAll();
        Iterable<Potion> potions = potionRepository.findAll();

        model.addAttribute("ingredients", ingredients );
        model.addAttribute("potions", potions );
        model.addAttribute("entities", StorageEntity.values());
        model.addAttribute("title", "Storage");
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
            storageCellCreate(cell);
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
        model.addAttribute("title", "Storage");
        return "storage-cell-edit";
    }
    @PostMapping("/storage/edit/{id}")
    public String storageCellEdit(@PathVariable(value = "id") long id, @RequestParam String quantity, Model model) {
        if (!storageCellRepository.existsById(id)) {
            return "redirect:/storage";
        }
        StorageCell cell = storageCellRepository.findById(id).orElseThrow();
        storageCellUpdate(cell, Long.parseLong(quantity));

        return "redirect:/storage";
    }

    @PostMapping("/storage/delete/{id}")
    public String potionDelete(@PathVariable(value = "id") long id) {
        if (!storageCellRepository.existsById(id)) {
            return "redirect:/storage";
        }
        StorageCell cell = storageCellRepository.findById(id).orElseThrow();
        storageCellRemove(cell);
        return "redirect:/storage";
    }

    public boolean storageCellCreate(StorageCell cell) {
        StorageCell savedCell = storageCellRepository.save(cell);
        return storageRecordCreateOperation(savedCell.getId(), StorageRecordOperation.CREATE, savedCell.getQuantity());
    }
    public boolean storageCellRemove(StorageCell cell) {
        storageCellRepository.delete(cell);
        return storageRecordCreateOperation(cell.getId(), StorageRecordOperation.REMOVE, cell.getQuantity());
    }
    public boolean storageCellUpdate(StorageCell cell, Long new_value) {
        long old_quantity = cell.getQuantity();
        if (old_quantity > new_value) {
           return storageCellSubtraction(cell, old_quantity-new_value);
        }
        if (old_quantity < new_value) {
            return storageCellAddition(cell, new_value-old_quantity);
        }
        return true;
    }

    public boolean storageCellAddition(StorageCell cell, Long operation_value) {
        if (!storageCellRepository.existsById(cell.getId())) {
            return false;
        }
        long new_quantity = cell.getQuantity() + operation_value;
        if (new_quantity < 0) {
            return false;
        }
        cell.setQuantity(new_quantity);
        storageCellRepository.save(cell);
        return storageRecordCreateOperation(cell.getId(), StorageRecordOperation.ADD, operation_value);
    }

    public boolean storageCellSubtraction(StorageCell cell, Long operation_value) {
        if (!storageCellRepository.existsById(cell.getId())) {
            return false;
        }
        long new_quantity = cell.getQuantity() - operation_value;
        if (new_quantity < 0) {
            return false;
        }
        cell.setQuantity(new_quantity);
        storageCellRepository.save(cell);
        return storageRecordCreateOperation(cell.getId(), StorageRecordOperation.SUBTRACTION, operation_value);
    }

    public boolean storageRecordRestoreOperation(StorageRecord record) {
        if (!storageRecordRepository.existsById(record.getId())) {
            return false;
        }
        switch(record.getOperation()){
            case SUBTRACTION:
                break;
            case ADD:
                break;
            case CREATE:
                break;
            case REMOVE:
                break;
            default:
                break;
        }

        return true;
    }

    public boolean storageRecordCreateOperation(Long cell_id, StorageRecordOperation operation, Long operation_value) {
        StorageRecord record = new StorageRecord(cell_id, operation, operation_value);
        storageRecordRepository.save(record);
        return storageRecordRepository.existsById(record.getId());
    }
}
