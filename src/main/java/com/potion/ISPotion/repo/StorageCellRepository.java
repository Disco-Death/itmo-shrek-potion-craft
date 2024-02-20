package com.potion.ISPotion.repo;

import com.potion.ISPotion.Classes.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface StorageCellRepository extends CrudRepository<StorageCell, Long> {
    Set<StorageCell> findAllByEntityAndTestApproved(StorageEntity entity, int testApproved);
    Set<StorageCell> findAllByOrderByCreationDateAsc();

    Set<StorageCell> findAllByEntityId(Long entityId);
}
