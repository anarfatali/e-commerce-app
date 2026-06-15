package az.company.ecommerceapp.mapper;

import az.company.ecommerceapp.dto.response.OrderItemResponse;
import az.company.ecommerceapp.dto.response.OrderResponse;
import az.company.ecommerceapp.dto.response.OrderSummaryResponse;
import az.company.ecommerceapp.dto.response.ShippingAddressResponse;
import az.company.ecommerceapp.model.entity.Order;
import az.company.ecommerceapp.model.entity.OrderItem;
import az.company.ecommerceapp.model.entity.ShippingAddress;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getPaymentStatus(),
                items,
                toAddressResponse(order.getShippingAddress()),
                order.getTotalAmount(),
                order.getNotes(),
                order.getCreatedDate(),
                order.getUpdatedDate()
        );
    }

    public OrderSummaryResponse toSummaryResponse(Order order) {
        return new OrderSummaryResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getTotalAmount(),
                order.getItems().size(),
                order.getCreatedDate()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        Long productId = item.getProduct() != null ? item.getProduct().getId() : null;

        return new OrderItemResponse(
                item.getId(),
                productId,
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getLineTotal()
        );
    }

    private ShippingAddressResponse toAddressResponse(ShippingAddress addr) {
        if (addr == null) return null;
        return new ShippingAddressResponse(
                addr.getFullName(),
                addr.getPhone(),
                addr.getStreet(),
                addr.getCity(),
                addr.getCountry(),
                addr.getPostalCode()
        );
    }
}