package az.company.ecommerceapp.dto.response;

import az.company.ecommerceapp.model.enums.OrderStatus;
import az.company.ecommerceapp.model.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String orderNumber,
        OrderStatus status,
        PaymentStatus paymentStatus,
        List<OrderItemResponse> items,
        ShippingAddressResponse shippingAddress,
        BigDecimal totalAmount,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}