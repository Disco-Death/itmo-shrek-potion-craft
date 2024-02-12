package com.potion.ISPotion.utils;

import com.potion.ISPotion.Classes.Task;
import com.potion.ISPotion.Classes.TaskStatus;
import com.potion.ISPotion.repo.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    void changeTaskStatus(Task task, TaskStatus newStatus) {
        task.setStatus(newStatus);

        if (TaskStatus.SENT_FOR_REVIEW.equals(newStatus)) {
            task.setDirectorTask(true);
        }

        taskRepository.save(task);
    }

    public void startTaskExecution(Task task) {
        changeTaskStatus(task, TaskStatus.STARTED);
    }

    public void sendForReview(Task task) {
        changeTaskStatus(task, TaskStatus.SENT_FOR_REVIEW);
    }

    public void startReview(Task task) {
        changeTaskStatus(task, TaskStatus.REVIEW_STARTED);
    }

    public void sendForRevision(Task task) {
        changeTaskStatus(task, TaskStatus.SENT_FOR_REWORK);
    }

    public void acceptCompletedTask(Task task) {
        changeTaskStatus(task, TaskStatus.ACCEPT_COMPLETED);
    }

    public List<Task> getDirectorTasks() {
        List<Task> allTasks = taskRepository.findAll();

        return allTasks.stream()
                .filter(task -> TaskStatus.SENT_FOR_REVIEW.equals(task.getStatus()))
                .collect(Collectors.toList());
    }
}

