package com.potion.ISPotion.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Set;

import com.potion.ISPotion.Classes.Task;
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByReviewerId(Long reviewerId);
    List<Task> findAllByExecutorId(Long executorId);
    Set<Task> findAllByOrderByCreationDateAsc();
}