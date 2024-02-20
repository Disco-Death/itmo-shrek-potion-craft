package com.potion.ISPotion.Classes;

public enum StorageEntity {
    Ingredient,
    Potion;

    public String toString() {
        if (this.equals(StorageEntity.Potion)) {
            return "Зелье";
        }
        if (this.equals(StorageEntity.Ingredient)) {
            return "Ингредиент";
        }
        return "";
    }
}