package com.potion.ISPotion.utils;

import com.potion.ISPotion.Classes.*;
import com.potion.ISPotion.repo.IngredientRepository;
import com.potion.ISPotion.repo.PotionRepository;
import com.potion.ISPotion.repo.StorageCellRepository;
import com.potion.ISPotion.repo.StorageRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class StorageService {
    @Autowired
    private PotionRepository potionRepository;
    @Autowired
    private IngredientRepository ingredientRepository;
    @Autowired
    private StorageCellRepository storageCellRepository;
    @Autowired
    private StorageRecordRepository storageRecordRepository;

    public boolean storageCellCreate(StorageCell cell) {
        StorageCell savedCell = storageCellRepository.save(cell);
        return storageRecordCreateOperation(savedCell.getId(), StorageRecordOperation.CREATE, savedCell.getQuantity());
    }
    public boolean storageCellRemove(StorageCell cell) {
        storageRecordsSetAllPreviousRestored(cell);
        storageCellRepository.delete(cell);
        return storageRecordCreateOperation(cell.getId(), StorageRecordOperation.REMOVE, cell.getQuantity());
    }
    public boolean storageCellUpdate(StorageCell cell, Long new_value) {
        long old_quantity = cell.getQuantity();
        if (old_quantity > new_value) {
            return storageCellSubtraction(cell, old_quantity-new_value, true);
        }
        if (old_quantity < new_value) {
            return storageCellAddition(cell, new_value-old_quantity, true);
        }
        return true;
    }

    public boolean storageCellAddition(StorageCell cell, Long operation_value, boolean withRecord) {
        if (!storageCellRepository.existsById(cell.getId())) {
            return false;
        }
        long new_quantity = cell.getQuantity() + operation_value;
        if (new_quantity < 0) {
            return false;
        }
        cell.setQuantity(new_quantity);
        storageCellRepository.save(cell);
        if (withRecord) {
            return storageRecordCreateOperation(cell.getId(), StorageRecordOperation.ADD, operation_value);
        }
        else return true;
    }

    public boolean storageCellSubtraction(StorageCell cell, Long operation_value, boolean withRecord) {
        if (!storageCellRepository.existsById(cell.getId())) {
            return false;
        }
        long new_quantity = cell.getQuantity() - operation_value;
        if (new_quantity < 0) {
            return false;
        }
        cell.setQuantity(new_quantity);
        storageCellRepository.save(cell);
        if (withRecord) {
            return storageRecordCreateOperation(cell.getId(), StorageRecordOperation.SUBTRACTION, operation_value);
        }
        else
            return true;
    }

    public boolean storageRecordRestoreOperation(StorageRecord record) {
        if (!storageRecordRepository.existsById(record.getId())) {
            return false;
        }
        switch(record.getOperation()){
            case SUBTRACTION:
                StorageCell cellSub = storageCellRepository.findById(record.getStorageCellId()).orElseThrow();
                storageCellAddition(cellSub, record.getOperation_value(), false);
                storageCellRepository.save(cellSub);

                record.setWas_restored(1);
                storageRecordRepository.save(record);
                break;
            case ADD:
                StorageCell cellAdd = storageCellRepository.findById(record.getStorageCellId()).orElseThrow();
                storageCellSubtraction(cellAdd, record.getOperation_value(), false);

                record.setWas_restored(1);
                storageRecordRepository.save(record);
                break;
            case CREATE, REMOVE:
            default:
                break;
        }

        return true;
    }

    public boolean storageRecordsSetAllPreviousRestored(StorageCell cell) {
        Iterable<StorageRecord> previousRecords = storageRecordRepository.findAllByStorageCellId(cell.getId());
        for (StorageRecord previousRecord: previousRecords
             ) {
            previousRecord.setWas_restored(1);
            storageRecordRepository.save(previousRecord);
        }
        return true;
    }

    public boolean storageRecordCreateOperation(Long cell_id, StorageRecordOperation operation, Long operation_value) {
        StorageRecord record = new StorageRecord(cell_id, operation, operation_value);
        storageRecordRepository.save(record);
        return storageRecordRepository.existsById(record.getId());
    }

    public Set<StorageCell> getAllStorageCellsForSale() {
        return storageCellRepository.findAllByEntityAndTestApproved(StorageEntity.Potion, 1);
    }

    public Iterable<Long> getAllPotionsIdsForSale() {
        var potionStorageCells = getAllStorageCellsForSale();
        var potionsIds = new HashSet<Long>();

        for (var potionStorageCell: potionStorageCells) {
            potionsIds.add(potionStorageCell.getEntityId());
        }

        return potionsIds;
    }

    public Iterable<StorageCell> getAllStorageCellsByEntityIdForSale(Long entityId) {
        Set<StorageCell> storageCells = getAllStorageCellsForSale();

        return storageCells.stream().filter(s -> s.getEntityId().equals(entityId)).toList();
    }

    public long sumPotionsQuantityInStorageForSaleByPotionId(long potionId) {
        var storageCells = getAllStorageCellsByEntityIdForSale(potionId);
        long sumQuantity = 0;

        for (var storageCell: storageCells) {
            sumQuantity += storageCell.getQuantity();
        }

        return sumQuantity;
    }

    public boolean isEnoughPotionsInStorageForSaleByPotionId(long potionId, long quantity) {
        return quantity <= sumPotionsQuantityInStorageForSaleByPotionId(potionId);
    }

    public boolean takePotionsFromStorageForSaleByPotionId(long potionId, long quantity) {
        if (!isEnoughPotionsInStorageForSaleByPotionId(potionId, quantity))
            return false;

        var storageCells = getAllStorageCellsByEntityIdForSale(potionId);

        for (var storageCell: storageCells) {
            long storageCellQuantity = storageCell.getQuantity();

            if (storageCellQuantity >= quantity) {
                storageCellSubtraction(storageCell, quantity, true);
                break;
            }

            storageCellSubtraction(storageCell, storageCellQuantity, true);
            quantity -= storageCellQuantity;
        }

        return true;
    }

    public void returnPotionsToStorageForSaleByPotionId(long potionId, long quantity) {
        StorageCell storageCell = new StorageCell(StorageEntity.Potion,  potionId, quantity);
        storageCellCreate(storageCell);
    }
}
