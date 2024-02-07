package com.potion.ISPotion.Classes;

import jakarta.persistence.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title, subject;
    @Column(length = 2048)
    private String  body;
    @Column(columnDefinition = "boolean default false")
    private boolean isSended;
    private Date creationDate;

    @PrePersist
    protected void onCreate() {
        creationDate = new Date();
    }

    public boolean isSended() {
        return isSended;
    }

    public void setSended(boolean sended) {
        isSended = sended;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Report(){

    }

    public  Report(String title, String subject, String body) {
        this.title = title;
        this.subject = subject;
        this.body = body;
        this.isSended = false;
    }

    public Date getCreationDate() {
        return creationDate;
    }
}
