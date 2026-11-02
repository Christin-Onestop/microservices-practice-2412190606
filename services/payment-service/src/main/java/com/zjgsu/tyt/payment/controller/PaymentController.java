package com.zjgsu.tyt.payment.controller;

import com.zjgsu.tyt.payment.entity.Payment;
import com.zjgsu.tyt.payment.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public List<Payment> getAllPayments() {
        return paymentService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(paymentService.findById(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> body) {
        try {
            Long taskId = Long.valueOf(body.get("taskId").toString());
            Long payerId = Long.valueOf(body.get("payerId").toString());
            Long payeeId = Long.valueOf(body.get("payeeId").toString());
            BigDecimal amount = new BigDecimal(body.get("amount").toString());

            Payment payment = paymentService.createPayment(
                    taskId, payerId, payeeId, amount);

            return ResponseEntity.status(HttpStatus.CREATED).body(payment);
        } catch (IllegalArgumentException | NullPointerException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "请求参数错误：" + e.getMessage()));
        }
    }
}