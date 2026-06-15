package az.company.ecommerceapp.service;

import az.company.ecommerceapp.dto.request.PlaceOrderRequest;
import az.company.ecommerceapp.dto.response.OrderResponse;
import az.company.ecommerceapp.dto.response.OrderSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderResponse placeOrder(Long userId, PlaceOrderRequest request);

    OrderResponse getOrder(Long userId, Long orderId);

    Page<OrderSummaryResponse> getUserOrders(Long userId, Pageable pageable);

    OrderResponse cancelOrder(Long userId, Long orderId);
}