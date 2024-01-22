package com.potion.ISPotion.Classes;


import com.potion.ISPotion.Controllers.IngredientController;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Entity
public class Potion {
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    @ManyToMany
    private  List<Ingredient> ingredients;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "potion")
    private List<Sale> sales;

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
    public Potion(String name, ArrayList<Long> ingredientsIds, ArrayList<Ingredient> ingredients) {
        this.name = name;
        this.ingredientsIds = ingredientsIds;
        this.ingredients = ingredients;
    }

    public Potion(String name) {
        this.name = name;
        this.ingredientsIds = new ArrayList<Long>();
        this.ingredients = new ArrayList<Ingredient>();
    }
}
