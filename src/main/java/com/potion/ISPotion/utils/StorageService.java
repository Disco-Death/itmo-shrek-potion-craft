package com.potion.ISPotion.utils;

import com.potion.ISPotion.Classes.StorageCell;
import com.potion.ISPotion.Classes.StorageEntity;
import com.potion.ISPotion.Classes.StorageRecord;
import com.potion.ISPotion.Classes.StorageRecordOperation;
import com.potion.ISPotion.repo.IngredientRepository;
import com.potion.ISPotion.repo.PotionRepository;
import com.potion.ISPotion.repo.StorageCellRepository;
import com.potion.ISPotion.repo.StorageRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        storageCellRepository.delete(cell);
        return storageRecordCreateOperation(cell.getId(), StorageRecordOperation.REMOVE, cell.getQuantity());
    }
    public boolean storageCellUpdate(StorageCell cell, Long new_value) {
        long old_quantity = cell.getQuantity();
        if (old_quantity > new_value) {
            return storageCellSubtraction(cell, old_quantity-new_value);
        }
        if (old_quantity < new_value) {
            return storageCellAddition(cell, new_value-old_quantity);
        }
        return true;
    }

    public boolean storageCellAddition(StorageCell cell, Long operation_value) {
        if (!storageCellRepository.existsById(cell.getId())) {
            return false;
        }
        long new_quantity = cell.getQuantity() + operation_value;
        if (new_quantity < 0) {
            return false;
        }
        cell.setQuantity(new_quantity);
        storageCellRepository.save(cell);
        return storageRecordCreateOperation(cell.getId(), StorageRecordOperation.ADD, operation_value);
    }

    public boolean storageCellSubtraction(StorageCell cell, Long operation_value) {
        if (!storageCellRepository.existsById(cell.getId())) {
            return false;
        }
        long new_quantity = cell.getQuantity() - operation_value;
        if (new_quantity < 0) {
            return false;
        }
        cell.setQuantity(new_quantity);
        storageCellRepository.save(cell);
        return storageRecordCreateOperation(cell.getId(), StorageRecordOperation.SUBTRACTION, operation_value);
    }

    public boolean storageRecordRestoreOperation(StorageRecord record) {
        if (!storageRecordRepository.existsById(record.getId())) {
            return false;
        }
        switch(record.getOperation()){
            case SUBTRACTION:
                break;
            case ADD:
                break;
            case CREATE:
                break;
            case REMOVE:
                break;
            default:
                break;
        }

        return true;
    }

    public boolean storageRecordCreateOperation(Long cell_id, StorageRecordOperation operation, Long operation_value) {
        StorageRecord record = new StorageRecord(cell_id, operation, operation_value);
        storageRecordRepository.save(record);
        return storageRecordRepository.existsById(record.getId());
    }

    public Set<StorageCell> getAllPotionsForSale() {
        return storageCellRepository.findAllByEntityAndTestApproved(StorageEntity.Potion, true);
    }
}
