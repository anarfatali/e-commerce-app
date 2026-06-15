package az.company.ecommerceapp.service.impl;

import az.company.ecommerceapp.dto.response.PaymentResponse;
import az.company.ecommerceapp.exception.ResourceNotFoundException;
import az.company.ecommerceapp.model.entity.Order;
import az.company.ecommerceapp.model.enums.OrderStatus;
import az.company.ecommerceapp.model.enums.PaymentStatus;
import az.company.ecommerceapp.repository.OrderRepository;
import az.company.ecommerceapp.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public PaymentResponse processPayment(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot pay for a cancelled order");
        }
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            throw new IllegalStateException("Order " + order.getOrderNumber() + " is already paid");
        }
        if (order.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException(
                    "Payment is not in PENDING state: " + order.getPaymentStatus());
        }

        String transactionId = "TXN-" + UUID.randomUUID().toString().toUpperCase();

        // ── Simulated payment gateway ──────────────────────────────────────────
        // Replace this block with a real provider call (Stripe, PayPal, etc.)
        // when you're ready. Everything below stays the same.
        log.info("[PAYMENT] Processing {} payment for order {} — amount: {} — txn: {}",
                order.getOrderNumber(),
                order.getTotalAmount(),
                transactionId);

        log.info("[PAYMENT] ✓ Payment successful — txn: {}", transactionId);
        // ──────────────────────────────────────────────────────────────────────

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);

        return new PaymentResponse(
                true,
                transactionId,
                "Payment processed successfully",
                PaymentStatus.PAID,
                OrderStatus.CONFIRMED
        );
    }
}