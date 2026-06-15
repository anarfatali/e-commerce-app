package az.company.ecommerceapp.mapper;

import az.company.ecommerceapp.dto.response.CartItemResponse;
import az.company.ecommerceapp.dto.response.CartResponse;
import az.company.ecommerceapp.model.entity.Cart;
import az.company.ecommerceapp.model.entity.CartItem;
import az.company.ecommerceapp.model.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CartMapper {

    public CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        BigDecimal subtotal = items.stream()
                .map(CartItemResponse::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::quantity)
                .sum();

        return new CartResponse(cart.getId(), items, totalItems, subtotal);
    }

    public CartItemResponse toItemResponse(CartItem item) {
        Product product = item.getProduct();
        BigDecimal unitPrice = resolveActivePrice(product);
        int qty = item.getQuantity();

        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getMainImageUrl(),
                unitPrice,
                qty,
                unitPrice.multiply(BigDecimal.valueOf(qty))
        );
    }

    private BigDecimal resolveActivePrice(Product product) {
        return product.getDiscountPrice() != null
                ? product.getDiscountPrice()
                : product.getOriginalPrice();
    }
}