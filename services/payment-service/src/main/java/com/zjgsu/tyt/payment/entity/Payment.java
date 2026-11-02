package com.zjgsu.tyt.payment.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long taskId;

    @Column(nullable = false)
    private Long payerId;

    @Column(nullable = false)
    private Long payeeId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    public Payment() {
    }

    public Payment(Long taskId, Long payerId, Long payeeId,
                   BigDecimal amount) {
        this.taskId = taskId;
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.amount = amount;
        this.status = "PENDING";
    }

    public Long getId() { return id; }
    public Long getTaskId() { return taskId; }
    public Long getPayerId() { return payerId; }
    public Long getPayeeId() { return payeeId; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public void setPayerId(Long payerId) { this.payerId = payerId; }
    public void setPayeeId(Long payeeId) { this.payeeId = payeeId; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setStatus(String status) { this.status = status; }
}