package az.company.ecommerceapp.service.impl;

import az.company.ecommerceapp.dto.request.ProductCreateRequest;
import az.company.ecommerceapp.dto.request.ProductUpdateRequest;
import az.company.ecommerceapp.dto.response.ProductAdminResponse;
import az.company.ecommerceapp.exception.DuplicateSlugException;
import az.company.ecommerceapp.exception.ResourceNotFoundException;
import az.company.ecommerceapp.mapper.AdminProductMapper;
import az.company.ecommerceapp.model.entity.Category;
import az.company.ecommerceapp.model.entity.Product;
import az.company.ecommerceapp.model.entity.ProductImage;
import az.company.ecommerceapp.repository.CategoryRepository;
import az.company.ecommerceapp.repository.ProductImageRepository;
import az.company.ecommerceapp.repository.ProductRepository;
import az.company.ecommerceapp.service.AdminProductService;
import az.company.ecommerceapp.service.ImageStorageService;
import az.company.ecommerceapp.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final ImageStorageService imageStorageService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    private final AdminProductMapper productMapper;

    @Override
    @Transactional
    public void createProduct(ProductCreateRequest request) {
        String slug = resolveSlug(request.slug(), request.name());
        if (productRepository.existsBySlug(slug)) {
            throw new DuplicateSlugException("Product slug already exists: " + slug);
        }

        validateDiscountPrice(request.price(), request.discountPrice());

        Category category = loadCategory(request.categoryId());

        Product product = new Product();
        product.setName(request.name());
        product.setSlug(slug);
        product.setDescription(request.description());
        product.setOriginalPrice(request.price());
        product.setDiscountPrice(request.discountPrice());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(category);
        product.setActive(request.active());

        productMapper.toAdminResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductAdminResponse updateProduct(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        if (request.name() != null) {
            product.setName(request.name());
        }

        if (request.slug() != null) {
            String newSlug = SlugUtils.from(request.slug());
            if (!newSlug.equals(product.getSlug()) && productRepository.existsBySlug(newSlug)) {
                throw new DuplicateSlugException("Product slug already exists: " + newSlug);
            }
            product.setSlug(newSlug);
        }

        if (request.price() != null) {
            product.setOriginalPrice(request.price());
        }

        if (request.discountPrice() != null) {
            // Use the updated price if it was also changed in this request
            BigDecimal effectivePrice = request.price() != null ? request.price() : product.getOriginalPrice();
            validateDiscountPrice(effectivePrice, request.discountPrice());
            product.setDiscountPrice(request.discountPrice());
        }

        if (request.stockQuantity() != null) {
            product.setStockQuantity(request.stockQuantity());
        }

        if (request.categoryId() != null) {
            product.setCategory(loadCategory(request.categoryId()));
        }

        if (request.active() != null) {
            product.setActive(request.active());
        }

        return productMapper.toAdminResponse(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));

        // Soft delete — order_items reference this product; hard delete would corrupt order history
        product.setActive(false);
        productRepository.save(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAdminResponse getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toAdminResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + id));
    }

    @Override
    @Transactional
    public void addImage(Long productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));

        var uploaded = imageStorageService.upload(file);

        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setUrl(uploaded.url());
        image.setPublicId(uploaded.publicId());

        productImageRepository.save(image);
    }

    @Override
    @Transactional
    public void deleteImage(Long productId, Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found: " + imageId));

        if (!image.getProduct().getId().equals(productId)) {
            throw new ResourceNotFoundException("Image not found: " + imageId);
        }

        productImageRepository.delete(image);
        imageStorageService.delete(image.getPublicId());
    }


    private String resolveSlug(String provided, String name) {
        return (provided != null && !provided.isBlank())
                ? SlugUtils.from(provided)
                : SlugUtils.from(name);
    }

    private Category loadCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryId));
    }

    private void validateDiscountPrice(BigDecimal price, BigDecimal discountPrice) {
        if (discountPrice == null) return;

        if (discountPrice.compareTo(price) > 0) {
            throw new IllegalArgumentException(
                    "Discount price cannot be greater than original price");
        }

        if (discountPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Discount price must be greater than zero");
        }

        if (discountPrice.compareTo(price) >= 0) {
            throw new IllegalArgumentException(
                    "Discount price must be less than original price");
        }
    }
}