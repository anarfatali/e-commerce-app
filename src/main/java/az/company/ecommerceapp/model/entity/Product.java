package az.company.ecommerceapp.model.entity;

import az.company.ecommerceapp.util.SlugUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "original_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "discount_price", precision = 12, scale = 2)
    private BigDecimal discountPrice;

    @Column(name = "main_image_url")
    private String mainImageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductImage> images = new ArrayList<>();

    @Column(name = "average_rating", nullable = false, precision = 3, scale = 2)
    @Builder.Default
    private BigDecimal averageRating = BigDecimal.ZERO;

    @Column(name = "review_count", nullable = false)
    @Builder.Default
    private int reviewCount = 0;

    @Column(name = "stock_quantity", nullable = false)
    @Builder.Default
    private int stockQuantity = 0;

    @Column(name = "is_new", nullable = false)
    @Builder.Default
    private boolean isNew = false;

    @Column(name = "is_best_seller", nullable = false)
    @Builder.Default
    private boolean bestSeller = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @PrePersist
    @PreUpdate
    private void normalizeSlug() {
        slug = SlugUtils.from(slug == null || slug.isBlank() ? name : slug);
    }
}
