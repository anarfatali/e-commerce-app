package az.company.ecommerceapp.service;

import az.company.ecommerceapp.dto.request.UpdateProfileRequest;
import az.company.ecommerceapp.dto.response.OrderResponse;
import az.company.ecommerceapp.dto.response.OrderSummaryResponse;
import az.company.ecommerceapp.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {


    UserResponse getCurrentUser(Long userId);

    UserResponse updateCurrentUser(Long userId, UpdateProfileRequest request);

    void deleteCurrentUser(Long userId);

    Page<OrderSummaryResponse> getOrders(Long userId, Pageable pageable);

    OrderResponse getOrderById(Long userId, Long orderId);

    UserResponse uploadProfilePicture(Long userId, MultipartFile file);

    UserResponse deleteProfilePicture(Long userId);

}