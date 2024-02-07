package com.potion.ISPotion.repo;

import com.potion.ISPotion.Classes.Sale;
import com.potion.ISPotion.Classes.StorageCell;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SaleRepository extends CrudRepository<Sale, Long> {
    Set<Sale> findAllByOrderByCreationDateAsc();
}
