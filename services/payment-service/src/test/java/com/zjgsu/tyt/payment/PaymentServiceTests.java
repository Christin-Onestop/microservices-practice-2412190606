package com.zjgsu.tyt.payment;

import com.zjgsu.tyt.payment.entity.Payment;
import com.zjgsu.tyt.payment.repository.PaymentRepository;
import com.zjgsu.tyt.payment.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTests {

    private PaymentRepository paymentRepository;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        paymentService = new PaymentService(paymentRepository);
    }

    @Test
    void createPaymentShouldSavePendingRecord() {
        Payment payment = new Payment(
                1L, 1L, 2L, new BigDecimal("5.00"));

        when(paymentRepository.save(any(Payment.class)))
                .thenReturn(payment);

        Payment result = paymentService.createPayment(
                1L, 1L, 2L, new BigDecimal("5.00"));

        verify(paymentRepository).save(any(Payment.class));
        assertEquals("PENDING", result.getStatus());
        assertEquals(new BigDecimal("5.00"), result.getAmount());
    }

    @Test
    void nonPositiveAmountShouldNotSave() {
        assertThrows(IllegalArgumentException.class, () ->
                paymentService.createPayment(
                        1L, 1L, 2L, BigDecimal.ZERO));

        verify(paymentRepository, never()).save(any(Payment.class));
    }

    @Test
    void payerAndPayeeCannotBeSame() {
        assertThrows(IllegalArgumentException.class, () ->
                paymentService.createPayment(
                        1L, 1L, 1L, new BigDecimal("5.00")));

        verifyNoInteractions(paymentRepository);
    }
}