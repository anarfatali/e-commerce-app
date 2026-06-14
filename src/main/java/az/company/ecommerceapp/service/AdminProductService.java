package az.company.ecommerceapp.service;

import az.company.ecommerceapp.dto.request.ProductCreateRequest;
import az.company.ecommerceapp.dto.request.ProductUpdateRequest;
import az.company.ecommerceapp.dto.response.ProductAdminResponse;
import org.springframework.web.multipart.MultipartFile;

public interface AdminProductService {

    void createProduct(ProductCreateRequest request);

    ProductAdminResponse updateProduct(Long id, ProductUpdateRequest request);

    void deleteProduct(Long id);

    ProductAdminResponse getProductById(Long id);

    void addImage(Long productId, MultipartFile file);

    void deleteImage(Long productId, Long imageId);
}