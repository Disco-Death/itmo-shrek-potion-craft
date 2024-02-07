package com.potion.ISPotion.repo;

import com.potion.ISPotion.Classes.Ingredient;
import com.potion.ISPotion.Classes.Potion;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface IngredientRepository extends CrudRepository<Ingredient, Long> {
    Set<Ingredient> findAllByOrderByCreationDateAsc();
}
