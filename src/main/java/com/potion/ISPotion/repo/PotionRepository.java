package com.potion.ISPotion.repo;

import com.potion.ISPotion.Classes.Potion;
import org.springframework.data.repository.CrudRepository;

public interface PotionRepository extends CrudRepository<Potion, Long> {
}