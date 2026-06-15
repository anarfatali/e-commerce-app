package az.company.ecommerceapp.service.impl;

import az.company.ecommerceapp.dto.request.PlaceOrderRequest;
import az.company.ecommerceapp.dto.request.ShippingAddressRequest;
import az.company.ecommerceapp.dto.response.OrderResponse;
import az.company.ecommerceapp.dto.response.OrderSummaryResponse;
import az.company.ecommerceapp.exception.ResourceNotFoundException;
import az.company.ecommerceapp.mapper.OrderMapper;
import az.company.ecommerceapp.model.entity.*;
import az.company.ecommerceapp.model.enums.OrderStatus;
import az.company.ecommerceapp.model.enums.PaymentStatus;
import az.company.ecommerceapp.repository.*;
import az.company.ecommerceapp.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository    orderRepository;
    private final CartRepository     cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository  productRepository;
    private final UserRepository     userRepository;
    private final OrderMapper        orderMapper;

    @Override
    @Transactional
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalStateException("Your cart is empty"));

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Your cart is empty");
        }

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            if (!product.isActive()) {
                throw new IllegalStateException(
                        "'" + product.getName() + "' is no longer available");
            }
            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException(
                        "Not enough stock for '" + product.getName() +
                                "'. Available: " + product.getStockQuantity());
            }
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        Order order = Order.builder()
                .user(user)
                .orderNumber(generateOrderNumber())
                .shippingAddress(toEmbeddable(request.shippingAddress()))
                .notes(request.notes())
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            BigDecimal unitPrice = resolveActivePrice(product);
            int qty = cartItem.getQuantity();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty));
            total = total.add(lineTotal);

            order.getItems().add(OrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .unitPrice(unitPrice)
                    .quantity(qty)
                    .lineTotal(lineTotal)
                    .build());

            product.setStockQuantity(product.getStockQuantity() - qty);
            productRepository.save(product);
        }

        order.setTotalAmount(total);
        Order saved = orderRepository.save(order);

        cart.getItems().clear();
        cartItemRepository.deleteAllByCartId(cart.getId());

        log.info("[ORDER] {} placed — user: {}, items: {}, total: {}",
                saved.getOrderNumber(), userId, saved.getItems().size(), total);

        return orderMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long userId, Long orderId) {
        return orderRepository.findByIdAndUserId(orderId, userId)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository
                .findByUserIdOrderByCreatedDateDesc(userId, pageable)
                .map(orderMapper::toSummaryResponse);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.PENDING
                && order.getStatus() != OrderStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Cannot cancel an order in '" + order.getStatus() + "' status");
        }

        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        }

        order.setStatus(OrderStatus.CANCELLED);

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        log.info("[ORDER] {} cancelled — user: {}", order.getOrderNumber(), userId);

        return orderMapper.toResponse(orderRepository.save(order));
    }


    private BigDecimal resolveActivePrice(Product product) {
        return product.getDiscountPrice() != null
                ? product.getDiscountPrice()
                : product.getOriginalPrice();
    }

    private ShippingAddress toEmbeddable(ShippingAddressRequest req) {
        return ShippingAddress.builder()
                .fullName(req.fullName())
                .phone(req.phone())
                .street(req.street())
                .city(req.city())
                .country(req.country())
                .postalCode(req.postalCode())
                .build();
    }

    private String generateOrderNumber() {
        String date   = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String suffix = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + date + "-" + suffix;
    }
}