package com.potion.ISPotion.Classes;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Entity
public class StorageRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long storageCellId;

    @Column(name = "operation")
    @Enumerated(EnumType.STRING)
    private StorageRecordOperation operation;

    private Long operation_value;

    public int getWas_restored() {
        return was_restored;
    }

    public void setWas_restored(int was_restored) {
        this.was_restored = was_restored;
    }

    private int was_restored;

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

    public Long getStorageCellId() {
        return storageCellId;
    }

    public void setStorageCellId(Long storageCellId) {
        this.storageCellId = storageCellId;
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

    public String getDate_add() {
        return date_add.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
    }

    public void setDate_add(Instant date_add) {
        this.date_add = date_add;
    }

    public String getDate_upd() {
        return date_upd.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
    }

    public void setDate_upd(Instant date_upd) {
        this.date_upd = date_upd;
    }

    public  StorageRecord() {

    }

    public  StorageRecord(Long storageCellId, StorageRecordOperation operation, Long operation_value) {
        this.storageCellId = storageCellId;
        this.operation = operation;
        this.operation_value = operation_value;
        this.was_restored = 0;
    }
}
