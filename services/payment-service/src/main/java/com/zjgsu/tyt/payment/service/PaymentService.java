package com.zjgsu.tyt.payment.service;

import com.zjgsu.tyt.payment.entity.Payment;
import com.zjgsu.tyt.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    public Payment findById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("支付记录不存在，ID：" + id));
    }

    public Payment createPayment(
            Long taskId, Long payerId, Long payeeId, BigDecimal amount) {

        if (taskId == null || taskId <= 0
                || payerId == null || payerId <= 0
                || payeeId == null || payeeId <= 0) {
            throw new IllegalArgumentException("任务 ID 和用户 ID 必须大于 0");
        }

        if (payerId.equals(payeeId)) {
            throw new IllegalArgumentException("付款人和收款人不能是同一人");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("支付金额必须大于 0");
        }

        Payment payment = new Payment(taskId, payerId, payeeId, amount);
        return paymentRepository.save(payment);
    }
}