package com.potion.ISPotion.Classes;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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

    @CreationTimestamp
    private Instant dateAdd;

    @CreationTimestamp
    private Instant dateUpd;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

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

    public String getDateAdd() {
        return dateAdd.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
    }

    public void setDateAdd(Instant dateAdd) {
        this.dateAdd = dateAdd;
    }

    public String getDateUpd() {
        return dateUpd.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss"));
    }

    public void setDateUpd(Instant dateUpd) {
        this.dateUpd = dateUpd;
    }

    public Report(){

    }

    public  Report(String title, String subject, String body, User user) {
        this.title = title;
        this.subject = subject;
        this.body = body;
        this.isSended = false;
        this.user = user;
    }

    public Date getCreationDate() {
        return Date.from(dateAdd);
    }
}
