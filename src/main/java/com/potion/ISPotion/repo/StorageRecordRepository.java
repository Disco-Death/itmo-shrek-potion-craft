package com.potion.ISPotion.repo;

import com.potion.ISPotion.Classes.Report;
import com.potion.ISPotion.Classes.StorageCell;
import com.potion.ISPotion.Classes.StorageEntity;
import com.potion.ISPotion.Classes.StorageRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface StorageRecordRepository extends CrudRepository<StorageRecord, Long> {
    Set<StorageRecord> findAllByStorageCellId(Long storage_cell_id);
}
