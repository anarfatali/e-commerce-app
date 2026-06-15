package az.company.ecommerceapp.mapper;

import az.company.ecommerceapp.dto.request.UpdateProfileRequest;
import az.company.ecommerceapp.dto.response.*;
import az.company.ecommerceapp.model.entity.Order;
import az.company.ecommerceapp.model.entity.OrderItem;
import az.company.ecommerceapp.model.entity.ShippingAddress;
import az.company.ecommerceapp.model.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {

    public void updateUserFromRequest(User user, UpdateProfileRequest request) {
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setPhoneNumber(request.phoneNumber());
        user.setDateOfBirth(request.dateOfBirth());
        user.setGender(request.gender());
    }

    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getDateOfBirth(),
                user.getGender(),
                user.getAvatarUrl(),
                user.getRole(),
                user.getStatus(),
                user.isEmailVerified()
        );
    }

    public OrderSummaryResponse toOrderSummary(Order order) {
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

    public OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(this::toOrderItemResponse)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getStatus(),
                order.getPaymentStatus(),
                items,
                toShippingAddressResponse(order.getShippingAddress()),
                order.getTotalAmount(),
                order.getNotes(),
                order.getCreatedDate(),
                order.getUpdatedDate()
        );
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getLineTotal()
        );
    }

    private ShippingAddressResponse toShippingAddressResponse(ShippingAddress address) {
        if (address == null) return null;
        return new ShippingAddressResponse(
                address.getFullName(),
                address.getStreet(),
                address.getCity(),
                address.getCountry(),
                address.getPostalCode(),
                address.getPhone()
        );
    }
}