package com.potion.ISPotion.Classes;

import jakarta.persistence.*;

@Entity
public class StorageCell {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "entity")
    @Enumerated(EnumType.STRING)
    private StorageEntity entity;
    private Long entity_id;

    private Long quantity;

    public int getTestApproved() {
        return testApproved;
    }

    public void setTestApproved(int testApproved) {
        this.testApproved = testApproved;
    }

    private int testApproved;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StorageEntity getEntity() {
        return entity;
    }

    public void setEntity(StorageEntity entity) {
        this.entity = entity;
    }

    public Long getEntity_id() {
        return entity_id;
    }

    public void setEntity_id(Long entity_id) {
        this.entity_id = entity_id;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public StorageCell() {
    }
    public StorageCell(StorageEntity entity, long entity_id, long quantity) {
        this.entity = entity;
        this.entity_id = entity_id;
        this.quantity = quantity;
    }
}
