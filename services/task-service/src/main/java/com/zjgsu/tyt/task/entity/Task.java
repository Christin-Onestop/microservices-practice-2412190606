package com.zjgsu.tyt.task.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal reward;

    @Column(nullable = false)
    private Long publisherId;

    private Long runnerId;

    @Column(nullable = false, length = 20)
    private String status = "OPEN";

    public Task() {
    }

    public Task(String title, String description,
                BigDecimal reward, Long publisherId) {
        this.title = title;
        this.description = description;
        this.reward = reward;
        this.publisherId = publisherId;
        this.status = "OPEN";
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public BigDecimal getReward() { return reward; }
    public Long getPublisherId() { return publisherId; }
    public Long getRunnerId() { return runnerId; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setReward(BigDecimal reward) { this.reward = reward; }
    public void setPublisherId(Long publisherId) { this.publisherId = publisherId; }
    public void setRunnerId(Long runnerId) { this.runnerId = runnerId; }
    public void setStatus(String status) { this.status = status; }
}