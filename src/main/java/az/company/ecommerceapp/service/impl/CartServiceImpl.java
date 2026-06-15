package az.company.ecommerceapp.service.impl;

import az.company.ecommerceapp.dto.request.AddToCartRequest;
import az.company.ecommerceapp.dto.request.UpdateCartItemRequest;
import az.company.ecommerceapp.dto.response.CartResponse;
import az.company.ecommerceapp.exception.ResourceNotFoundException;
import az.company.ecommerceapp.mapper.CartMapper;
import az.company.ecommerceapp.model.entity.Cart;
import az.company.ecommerceapp.model.entity.CartItem;
import az.company.ecommerceapp.model.entity.Product;
import az.company.ecommerceapp.model.entity.User;
import az.company.ecommerceapp.repository.CartItemRepository;
import az.company.ecommerceapp.repository.CartRepository;
import az.company.ecommerceapp.repository.ProductRepository;
import az.company.ecommerceapp.repository.UserRepository;
import az.company.ecommerceapp.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartMapper cartMapper;

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .map(cartMapper::toResponse)
                .orElse(new CartResponse(null, List.of(), 0, BigDecimal.ZERO));
    }

    @Override
    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        Product product = productRepository.findById(request.productId())
                .filter(Product::isActive)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found: " + request.productId()));

        Cart cart = getOrCreateCart(userId);

        Optional<CartItem> existing =
                cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

        if (existing.isPresent()) {
            CartItem item = existing.get();
            int newQty = item.getQuantity() + request.quantity();
            validateStock(product, newQty);
            item.setQuantity(newQty);
            cartItemRepository.save(item);
        } else {
            validateStock(product, request.quantity());
            CartItem item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .build();
            cart.getItems().add(cartItemRepository.save(item));
        }

        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        Cart cart = requireCart(userId);
        CartItem item = requireOwnedItem(cart, itemId);

        if (request.quantity() == 0) {
            cart.getItems().removeIf(i -> i.getId().equals(itemId));
            cartItemRepository.delete(item);
        } else {
            validateStock(item.getProduct(), request.quantity());
            item.setQuantity(request.quantity());
            cartItemRepository.save(item);
        }

        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(Long userId, Long itemId) {
        Cart cart = requireCart(userId);
        CartItem item = requireOwnedItem(cart, itemId);
        cart.getItems().removeIf(i -> i.getId().equals(itemId));
        cartItemRepository.delete(item);
        return cartMapper.toResponse(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.getItems().clear();
            cartItemRepository.deleteAllByCartId(cart.getId());
        });
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
            return cartRepository.save(Cart.builder().user(user).build());
        });
    }

    private Cart requireCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));
    }

    private CartItem requireOwnedItem(Cart cart, Long itemId) {
        return cartItemRepository.findById(itemId)
                .filter(i -> i.getCart().getId().equals(cart.getId()))
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found: " + itemId));
    }

    private void validateStock(Product product, int requestedQty) {
        if (product.getStockQuantity() < requestedQty) {
            throw new IllegalStateException(
                    "Not enough stock for '" + product.getName() +
                            "'. Available: " + product.getStockQuantity());
        }
    }
}