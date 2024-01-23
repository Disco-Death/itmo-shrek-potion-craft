package com.potion.ISPotion.utils;

import com.potion.ISPotion.Classes.Task;
import com.potion.ISPotion.Classes.TaskStatus;
import com.potion.ISPotion.repo.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    public void changeTaskStatus(Task task, TaskStatus newStatus) {
        task.setStatus(newStatus);
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
}

