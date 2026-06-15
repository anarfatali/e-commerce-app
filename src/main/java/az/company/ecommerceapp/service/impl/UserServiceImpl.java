package az.company.ecommerceapp.service.impl;

import az.company.ecommerceapp.dto.request.UpdateProfileRequest;
import az.company.ecommerceapp.dto.response.OrderResponse;
import az.company.ecommerceapp.dto.response.OrderSummaryResponse;
import az.company.ecommerceapp.dto.response.UserResponse;
import az.company.ecommerceapp.mapper.UserMapper;
import az.company.ecommerceapp.model.entity.Order;
import az.company.ecommerceapp.model.entity.User;
import az.company.ecommerceapp.model.enums.UserStatus;
import az.company.ecommerceapp.repository.OrderRepository;
import az.company.ecommerceapp.repository.UserRepository;
import az.company.ecommerceapp.service.ImageStorageService;
import az.company.ecommerceapp.service.ImageStorageService.UploadResult;
import az.company.ecommerceapp.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ImageStorageService storageService;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Long userId) {
        return userMapper.toUserResponse(findActiveUser(userId));
    }

    @Override
    @Transactional
    public UserResponse updateCurrentUser(Long userId, UpdateProfileRequest request) {
        User user = findActiveUser(userId);

        userMapper.updateUserFromRequest(user, request);

        log.info("User profile updated: userId={}", userId);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteCurrentUser(Long userId) {
        User user = findActiveUser(userId);

        user.setDeletedAt(LocalDateTime.now());
        user.setStatus(UserStatus.DELETED);

        if (StringUtils.hasText(user.getAvatarPublicId())) {
            safeDeleteAvatar(user.getAvatarPublicId());
            user.setAvatarUrl(null);
            user.setAvatarPublicId(null);
        }

        userRepository.save(user);
        log.info("User soft-deleted: userId={}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderSummaryResponse> getOrders(Long userId, Pageable pageable) {
        findActiveUser(userId);
        return orderRepository.findAllByUserId(userId, pageable)
                .map(userMapper::toOrderSummary);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long userId, Long orderId) {
        findActiveUser(userId);
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Order not found: id=%d, userId=%d".formatted(orderId, userId)));
        return userMapper.toOrderResponse(order);
    }

    @Override
    @Transactional
    public UserResponse uploadProfilePicture(Long userId, MultipartFile file) {
        User user = findActiveUser(userId);

        if (StringUtils.hasText(user.getAvatarPublicId())) {
            safeDeleteAvatar(user.getAvatarPublicId());
        }

        UploadResult result = storageService.upload(file);
        user.setAvatarUrl(result.url());
        user.setAvatarPublicId(result.publicId());

        log.info("Profile picture uploaded: userId={}, publicId={}", userId, result.publicId());
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse deleteProfilePicture(Long userId) {
        User user = findActiveUser(userId);

        if (!StringUtils.hasText(user.getAvatarPublicId())) {
            throw new IllegalStateException("User has no profile picture to delete");
        }

        safeDeleteAvatar(user.getAvatarPublicId());
        user.setAvatarUrl(null);
        user.setAvatarPublicId(null);

        log.info("Profile picture deleted: userId={}", userId);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    private User findActiveUser(Long userId) {
        return userRepository.findById(userId)
                .filter(u -> u.getDeletedAt() == null)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));
    }

    private void safeDeleteAvatar(String publicId) {
        try {
            storageService.delete(publicId);
        } catch (Exception ex) {
            log.warn("Failed to delete avatar from Cloudinary: publicId={}, reason={}", publicId, ex.getMessage());
        }
    }
}