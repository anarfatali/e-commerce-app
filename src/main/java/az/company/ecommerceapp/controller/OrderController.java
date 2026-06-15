package az.company.ecommerceapp.controller;

import az.company.ecommerceapp.dto.request.PlaceOrderRequest;
import az.company.ecommerceapp.dto.response.OrderResponse;
import az.company.ecommerceapp.dto.response.OrderSummaryResponse;
import az.company.ecommerceapp.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid PlaceOrderRequest request) {
        orderService.placeOrder(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<Page<OrderSummaryResponse>> getUserOrders(
            @AuthenticationPrincipal Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                orderService.getUserOrders(userId, PageRequest.of(page, size)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrder(userId, orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(userId, orderId));
    }
}