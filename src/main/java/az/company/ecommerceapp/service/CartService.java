package az.company.ecommerceapp.service;

import az.company.ecommerceapp.dto.request.AddToCartRequest;
import az.company.ecommerceapp.dto.request.UpdateCartItemRequest;
import az.company.ecommerceapp.dto.response.CartResponse;

public interface CartService {

    CartResponse getCart(Long userId);

    CartResponse addToCart(Long userId, AddToCartRequest request);

    CartResponse updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request);

    CartResponse removeCartItem(Long userId, Long itemId);

    void clearCart(Long userId);
}