package az.company.ecommerceapp.dto.response;

import az.company.ecommerceapp.model.enums.OrderStatus;
import az.company.ecommerceapp.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryResponse(
        Long id,
        String orderNumber,
        OrderStatus status,
        PaymentStatus paymentStatus,
        BigDecimal totalAmount,
        int itemCount,
        LocalDateTime createdAt
) {}