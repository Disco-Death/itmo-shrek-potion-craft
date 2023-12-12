package com.potion.ISPotion.Controllers;

import com.potion.ISPotion.Classes.Potion;
import com.potion.ISPotion.Classes.Sale;
import com.potion.ISPotion.repo.PotionRepository;
import com.potion.ISPotion.repo.SaleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SaleController {
    @Autowired
    private SaleRepository saleRepository;
    @Autowired
    private PotionRepository potionRepository;

    @GetMapping("/sale")
    public String sale(Model model) {
        Iterable<Sale> sales = saleRepository.findAll();

        model.addAttribute("title", "Продажи");
        model.addAttribute("sales", sales);

        return "sale";
    }

    @GetMapping("/sale/{id}")
    public String saleDisplay(@PathVariable(value = "id") long id, Model model) {
        Sale sale = saleRepository.findById(id).orElseThrow();

        model.addAttribute("title", "Продажа");
        model.addAttribute("sale", sale);

        return "sale-details";
    }

    @GetMapping("/sale/add")
    public String saleAddDisplay(Model model) {
        Iterable<Potion> potions = potionRepository.findAll();

        model.addAttribute("title", "Добавить продажу");
        model.addAttribute("potions", potions);

        return "sale-add";
    }

    @PostMapping("/sale/add")
    public String saleAdd(@RequestParam Long potionId,
                          @RequestParam Long quantity,
                          @RequestParam Long price,
                          @RequestParam String client,
                          Model model) {
        if (!potionRepository.existsById(potionId)) {
            return "redirect:/sale";
        }

        Potion potion = potionRepository.findById(potionId).orElseThrow();
        Sale sale = new Sale(potion, quantity, price, client);
        saleRepository.save(sale);

        return "redirect:/sale";
    }

    @GetMapping("/sale/edit/{id}")
    public String saleEditDisplay(@PathVariable(value = "id") long id, Model model) {
        if (!saleRepository.existsById(id)) {
            return "redirect:/sale";
        }

        Sale sale = saleRepository.findById(id).orElseThrow();
        Iterable<Potion> potions = potionRepository.findAll();

        model.addAttribute("title", "Изменить продажу");
        model.addAttribute("sale", sale );
        model.addAttribute("potions", potions);

        return "sale-edit";
    }

    @PostMapping("/sale/edit/{id}")
    public String saleEdit(@PathVariable(value = "id") long id,
                           @RequestParam Long potionId,
                           @RequestParam Long quantity,
                           @RequestParam Long price,
                           @RequestParam String client,
                           Model model) {
        if (!saleRepository.existsById(id)) {
            return "redirect:/sale";
        }
        if (!potionRepository.existsById(potionId)) {
            return "redirect:/sale";
        }

        Potion potion = potionRepository.findById(potionId).orElseThrow();
        Sale sale = saleRepository.findById(id).orElseThrow();

        sale.setPotion(potion);
        sale.setQuantity(quantity);
        sale.setPrice(price);
        sale.setClient(client);
        saleRepository.save(sale);

        return "redirect:/sale";
    }

    @PostMapping("/sale/delete/{id}")
    public String saleDelete(@PathVariable(value = "id") long id) {
        if (!saleRepository.existsById(id)) {
            return "redirect:/sale";
        }
        saleRepository.deleteById(id);

        return "redirect:/sale";
    }
}
