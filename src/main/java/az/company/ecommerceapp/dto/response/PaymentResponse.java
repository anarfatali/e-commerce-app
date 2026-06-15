package az.company.ecommerceapp.dto.response;

import az.company.ecommerceapp.model.enums.OrderStatus;
import az.company.ecommerceapp.model.enums.PaymentStatus;

public record PaymentResponse(
        boolean success,
        String transactionId,
        String message,
        PaymentStatus paymentStatus,
        OrderStatus orderStatus
) {
}