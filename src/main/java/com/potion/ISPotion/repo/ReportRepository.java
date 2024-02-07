package com.potion.ISPotion.repo;

import com.potion.ISPotion.Classes.Report;
import com.potion.ISPotion.Classes.Sale;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ReportRepository extends CrudRepository<Report, Long> {
    Set<Report> findAllByOrderByCreationDateAsc();
}
