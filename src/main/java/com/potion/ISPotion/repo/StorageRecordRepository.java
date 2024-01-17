package com.potion.ISPotion.repo;

import com.potion.ISPotion.Classes.Report;
import com.potion.ISPotion.Classes.StorageCell;
import com.potion.ISPotion.Classes.StorageRecord;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRecordRepository extends CrudRepository<StorageRecord, Long> {
}
