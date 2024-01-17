package com.potion.ISPotion.repo;

import com.potion.ISPotion.Classes.Report;
import com.potion.ISPotion.Classes.StorageCell;
import com.potion.ISPotion.Classes.StorageEntity;
import com.potion.ISPotion.Classes.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface StorageCellRepository extends CrudRepository<StorageCell, Long> {
    Set<StorageCell> findAllByEntityAndTestApproved(StorageEntity entity, boolean testApproved);
}
