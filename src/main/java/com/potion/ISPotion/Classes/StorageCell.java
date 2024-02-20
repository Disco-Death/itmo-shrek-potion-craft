package com.potion.ISPotion.Classes;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class StorageCell {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "entity")
    @Enumerated(EnumType.STRING)
    private StorageEntity entity;
    private Long entityId;

    private Long quantity;
    private Date creationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = new Date();
    }

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

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public StorageCell() {
    }
    public StorageCell(StorageEntity entity, long entityId, long quantity) {
        this.entity = entity;
        this.entityId = entityId;
        this.quantity = quantity;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}
