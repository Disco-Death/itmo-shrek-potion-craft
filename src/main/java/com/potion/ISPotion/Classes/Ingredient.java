
package com.potion.ISPotion.Classes;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name, property;
    private Date creationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = new Date();
    }

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

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
    public Ingredient() {
    }
    public Ingredient(String name, String property) {
        this.name = name;
        this. property = property;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}

