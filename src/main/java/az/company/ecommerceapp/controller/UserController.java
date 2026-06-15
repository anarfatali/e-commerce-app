package az.company.ecommerceapp.controller;

import az.company.ecommerceapp.dto.request.UpdateProfileRequest;
import az.company.ecommerceapp.dto.response.OrderResponse;
import az.company.ecommerceapp.dto.response.OrderSummaryResponse;
import az.company.ecommerceapp.dto.response.UserResponse;
import az.company.ecommerceapp.service.UserService;
import az.company.ecommerceapp.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final SecurityUtils securityUtils;

    @GetMapping
    public ResponseEntity<UserResponse> getMe() {
        return ResponseEntity.ok(userService.getCurrentUser(securityUtils.getCurrentUserId()));
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateCurrentUser(securityUtils.getCurrentUserId(), request));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteMe() {
        userService.deleteCurrentUser(securityUtils.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<OrderSummaryResponse>> getMyOrders(
            @PageableDefault(size = 10, sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(userService.getOrders(securityUtils.getCurrentUserId(), pageable));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<OrderResponse> getMyOrder(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getOrderById(securityUtils.getCurrentUserId(), id));
    }

    @PutMapping(
            value = "/profile-picture",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<UserResponse> uploadProfilePicture(@RequestPart("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadProfilePicture(securityUtils.getCurrentUserId(), file));
    }

    @DeleteMapping("/profile-picture")
    public ResponseEntity<UserResponse> deleteProfilePicture() {
        return ResponseEntity.ok(userService.deleteProfilePicture(securityUtils.getCurrentUserId()));
    }
}