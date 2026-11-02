package com.zjgsu.tyt.payment.repository;

import com.zjgsu.tyt.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}