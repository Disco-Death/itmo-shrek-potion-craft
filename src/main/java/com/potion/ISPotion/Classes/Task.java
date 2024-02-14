package com.potion.ISPotion.Classes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;

import java.util.Date;

import java.time.LocalDateTime;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @ManyToOne
    @JoinColumn(name = "executor_id")
    private User executor;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    private Date creationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = new Date();
    }

    private LocalDateTime deadline;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_director_task")
    private boolean isDirectorTask;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getExecutor() {
        return executor;
    }

    public void setExecutor(User executor) {
        this.executor = executor;
    }

    public User getReviewer() {
        return reviewer;
    }

    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDirectorTask() {
        return isDirectorTask;
    }

    public void setDirectorTask(boolean directorTask) {
        isDirectorTask = directorTask;
    }

    public String getStatusName() {
        switch (status) {
            case ASSIGNED -> { return "Назначено"; }
            case STARTED -> { return "Начато"; }
            case SENT_FOR_REVIEW -> { return "Отправлено на рецензию"; }
            case REVIEW_STARTED -> { return "Рецензия начата"; }
            case SENT_FOR_REWORK -> { return "Отправлено на доработку"; }
            case ACCEPT_COMPLETED -> { return "Завершено и принято"; }
        }
        return "НЕТ СТАТУСА";
    }
}