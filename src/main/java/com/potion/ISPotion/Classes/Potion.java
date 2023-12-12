package com.potion.ISPotion.Classes;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;

@Entity
public class Potion {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    public ArrayList<Long> getIngredientsIds() {
        return ingredientsIds;
    }

    public void setIngredientsIds(ArrayList<Long> ingredientsIds) {
        this.ingredientsIds = ingredientsIds;
    }


    @JdbcTypeCode(SqlTypes.JSON)
    private ArrayList<Long> ingredientsIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addIngredientId(Long ingredientId) {
        this.ingredientsIds.add(ingredientId);
    }

    public void deleteIngredientId(Long ingredientId) {
        this.ingredientsIds.remove(ingredientId);
    }

    public Potion() {
    }
    public Potion(String name, ArrayList<Long> ingredientsIds) {
        this.name = name;
        this.ingredientsIds = ingredientsIds;
    }

    public Potion(String name) {
        this.name = name;
        this.ingredientsIds = new ArrayList<Long>();
    }
}
