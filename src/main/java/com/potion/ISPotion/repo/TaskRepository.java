package com.potion.ISPotion.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.potion.ISPotion.Classes.Task;
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
}