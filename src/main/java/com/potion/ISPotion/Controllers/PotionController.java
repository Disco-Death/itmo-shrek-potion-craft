package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.Ingredient;
import com.potion.ISPotion.Classes.Potion;
import com.potion.ISPotion.Classes.StorageCell;
import com.potion.ISPotion.repo.IngredientRepository;
import com.potion.ISPotion.repo.PotionRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;

@Controller
public class PotionController {
    @Autowired
    private PotionRepository potionRepository;
    @Autowired
    private IngredientRepository ingredientRepository;

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }
    @GetMapping("/potion/add")
    public String potionDisplayAdd(Model model) {
        Iterable<Ingredient> ingredients = ingredientRepository.findAll();
        model.addAttribute("ingredients", ingredients );
        model.addAttribute("title", "potion");
        return "potion-add";
    }


    @GetMapping("/potion")
    public String potion(Model model) {
        Iterable<Potion> potions = potionRepository.findAll();
        model.addAttribute("potions", potions );
        model.addAttribute("title", "potion");
        return "potion";
    }
    @GetMapping("/potion/edit/{id}")
    public String potionEditDisplay(@PathVariable(value = "id") long id, Model model) {
        if (!potionRepository.existsById(id)) {
            return "redirect:/potion";
        }
        Potion potion = potionRepository.findById(id).orElseThrow();
        Iterable<Ingredient> ingredients = ingredientRepository.findAll();

        model.addAttribute("ingredients", ingredients );
        model.addAttribute("potion", potion );
        model.addAttribute("title", "potion");
        return "potion-edit";
    }
    @PostMapping("/potion/edit/{id}")
    public String potionEdit(@PathVariable(value = "id") long id, HttpServletRequest request, Model model) {
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
    public String potionAdd( HttpServletRequest request, Model model) {
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
    public String potionDelete(@PathVariable(value = "id") long id) {
        if (!potionRepository.existsById(id)) {
            return "redirect:/potion";
        }
        potionRepository.deleteById(id);
        return "redirect:/potion";
    }
}
