package com.potion.ISPotion.Classes;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
public class StorageRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long storage_cell_id;

    @Column(name = "operation")
    @Enumerated(EnumType.STRING)
    private StorageRecordOperation operation;

    private Long operation_value;

    @CreationTimestamp
    private Instant date_add;

    @CreationTimestamp
    private Instant date_upd;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStorage_cell_id() {
        return storage_cell_id;
    }

    public void setStorage_cell_id(Long storage_cell_id) {
        this.storage_cell_id = storage_cell_id;
    }

    public StorageRecordOperation getOperation() {
        return operation;
    }

    public void setOperation(StorageRecordOperation operation) {
        this.operation = operation;
    }

    public Long getOperation_value() {
        return operation_value;
    }

    public void setOperation_value(Long operation_value) {
        this.operation_value = operation_value;
    }

    public Instant getDate_add() {
        return date_add;
    }

    public void setDate_add(Instant date_add) {
        this.date_add = date_add;
    }

    public Instant getDate_upd() {
        return date_upd;
    }

    public void setDate_upd(Instant date_upd) {
        this.date_upd = date_upd;
    }

    public  StorageRecord() {

    }

    public  StorageRecord(Long storage_cell_id, StorageRecordOperation operation, Long operation_value) {
        this.storage_cell_id = storage_cell_id;
        this.operation = operation;
        this.operation_value = operation_value;
    }
}
