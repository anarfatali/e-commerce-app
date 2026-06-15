package az.company.ecommerceapp.service;

import az.company.ecommerceapp.dto.response.PaymentResponse;

public interface PaymentService {

    PaymentResponse processPayment(Long userId, Long orderId);
}
