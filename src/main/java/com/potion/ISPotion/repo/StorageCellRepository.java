package com.potion.ISPotion.repo;

import com.potion.ISPotion.Classes.Report;
import com.potion.ISPotion.Classes.StorageCell;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageCellRepository extends CrudRepository<StorageCell, Long> {
}
