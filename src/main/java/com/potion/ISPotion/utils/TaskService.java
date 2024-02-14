package com.potion.ISPotion.utils;

import com.potion.ISPotion.Classes.Task;
import com.potion.ISPotion.Classes.TaskStatus;
import com.potion.ISPotion.repo.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.ListUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public List<Task> getTaskByReviewerIdOnReview(long reviewerId) {
        List<Task> tasks = taskRepository.findAllByReviewerId(reviewerId);

        return tasks.stream()
                .filter(task -> TaskStatus.SENT_FOR_REVIEW.equals(task.getStatus()))
                .collect(Collectors.toList());
    }

    public List<Task> getAllTasksByReviewerIdAndExecutorId(long userId) {
        var reviewerTasks = taskRepository.findAllByReviewerId(userId);
        var executorTasks = taskRepository.findAllByExecutorId(userId);

        return Stream
                .concat(reviewerTasks.stream(), executorTasks.stream())
                .distinct()
                .collect(Collectors.toList());
    }
}

