package az.company.ecommerceapp.controller;

import az.company.ecommerceapp.dto.response.PaymentResponse;
import az.company.ecommerceapp.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/{orderId}")
    public ResponseEntity<PaymentResponse> processPayment(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(paymentService.processPayment(userId, orderId));
    }
}