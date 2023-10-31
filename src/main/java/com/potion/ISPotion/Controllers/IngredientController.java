package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.potion.ISPotion.repo.IngredientRepository;

import java.util.ArrayList;
import java.util.Optional;

@Controller
public class IngredientController {
    @Autowired
    private IngredientRepository ingredientRepository;

    @GetMapping("/ingredient/add")
    public String ingredientDisplayAdd(Model model) {
        return "ingredient-add";
    }


    @GetMapping("/ingredient")
    public String ingredient(Model model) {
        Iterable<Ingredient> ingredients = ingredientRepository.findAll();
        model.addAttribute("ingredients", ingredients );
        model.addAttribute("title", "ingredient");
        return "ingredient";
    }
    @GetMapping("/ingredient/edit/{id}")
    public String ingredientEditDisplay(@PathVariable(value = "id") long id, Model model) {
        if (!ingredientRepository.existsById(id)) {
            return "redirect:/ingredient";
        }
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow();
        model.addAttribute("ingredient", ingredient );
        model.addAttribute("title", "ingredient");
        return "ingredient-edit";
    }
    @PostMapping("/ingredient/edit/{id}")
    public String ingredientEdit(@PathVariable(value = "id") long id, @RequestParam String name, @RequestParam String property, Model model) {
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
    public String ingredientAdd(@RequestParam String name, @RequestParam String property, Model model) {
        Ingredient ingredient = new Ingredient(name, property) ;
        ingredientRepository.save(ingredient);
        return "redirect:/ingredient";
    }
    @PostMapping("/ingredient/delete/{id}")
    public String ingredientDelete(@PathVariable(value = "id") long id) {
        if (!ingredientRepository.existsById(id)) {
            return "redirect:/ingredient";
        }
        ingredientRepository.deleteById(id);
        return "redirect:/ingredient";
    }
}
