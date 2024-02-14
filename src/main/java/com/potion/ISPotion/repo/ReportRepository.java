package com.potion.ISPotion.repo;

import com.potion.ISPotion.Classes.Report;
import com.potion.ISPotion.Classes.Sale;
import com.potion.ISPotion.Classes.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface ReportRepository extends CrudRepository<Report, Long> {
    Set<Report> findAllByOrderByDateAddAsc();
    Set<Report> findAllByUser(User user);
    Set<Report> findAllByIsSended(Boolean isSended);

    Set<Report> findAllByUserAndIsSended(User user, Boolean isSended);
}
