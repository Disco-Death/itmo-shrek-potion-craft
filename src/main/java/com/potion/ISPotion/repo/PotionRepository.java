package com.potion.ISPotion.repo;

import com.potion.ISPotion.Classes.Potion;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface PotionRepository extends CrudRepository<Potion, Long> {
    Set<Potion> findAllByOrderByCreationDateAsc();
}