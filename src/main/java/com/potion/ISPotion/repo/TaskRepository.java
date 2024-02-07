package com.potion.ISPotion.repo;

import com.potion.ISPotion.Classes.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Set;

import com.potion.ISPotion.Classes.Task;
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    Set<Task> findAllByOrderByCreationDateAsc();
}