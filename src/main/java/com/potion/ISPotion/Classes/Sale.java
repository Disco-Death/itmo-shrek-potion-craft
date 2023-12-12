package com.potion.ISPotion.Classes;

import jakarta.persistence.*;

@Entity
@Table(name = "sale")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long potionId;

    private Long quantity;

    private Long price;

    @Column(length = 512)
    private String client;

    public Sale() { }

    public Sale(Long potionId, Long quantity, Long price, String client) {
        this.potionId = potionId;
        this.quantity = quantity;
        this.price = price;
        this.client = client;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPotionId() {
        return potionId;
    }

    public void setPotionId(Long potionId) {
        this.potionId = potionId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}
