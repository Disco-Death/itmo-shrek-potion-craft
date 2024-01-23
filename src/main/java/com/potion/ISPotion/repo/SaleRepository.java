package com.potion.ISPotion.repo;

import com.potion.ISPotion.Classes.Sale;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SaleRepository extends CrudRepository<Sale, Long> {
}
