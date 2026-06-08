package az.company.ecommerceapp.repository.specification;

import az.company.ecommerceapp.model.entity.Product;
import az.company.ecommerceapp.model.enums.ProductSortOption;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public final class ProductSpecifications {

    private ProductSpecifications() {
    }

    public static Specification<Product> publicCatalog(
            Collection<Long> categoryIds,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            ProductSortOption sort,
            Boolean isNew,
            Boolean bestSeller,
            String searchText) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            Expression<BigDecimal> activePrice = activePrice(root, cb);

            predicates.add(cb.isTrue(root.get("active")));

            if (categoryIds != null && !categoryIds.isEmpty()) {
                predicates.add(root.get("category").get("id").in(categoryIds));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(activePrice, minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(activePrice, maxPrice));
            }

            if (isNew != null) {
                predicates.add(cb.equal(root.get("isNew"), isNew));
            }

            if (bestSeller != null) {
                predicates.add(cb.equal(root.get("bestSeller"), bestSeller));
            }

            if (searchText != null && !searchText.isBlank()) {
                String pattern = "%" + searchText.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                ));
            }

            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                applySorting(root, query, cb, activePrice, sort);
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static Expression<BigDecimal> activePrice(Root<Product> root, CriteriaBuilder cb) {
        CriteriaBuilder.Coalesce<BigDecimal> activePrice = cb.coalesce();
        activePrice.value(root.get("discountPrice"));
        activePrice.value(root.get("originalPrice"));
        return activePrice;
    }

    private static void applySorting(
            Root<Product> root,
            jakarta.persistence.criteria.CriteriaQuery<?> query,
            CriteriaBuilder cb,
            Expression<BigDecimal> activePrice,
            ProductSortOption sort) {
        ProductSortOption sortOption = sort == null ? ProductSortOption.NEWEST : sort;

        switch (sortOption) {
            case PRICE_LOW_TO_HIGH -> query.orderBy(cb.asc(activePrice), cb.desc(root.get("id")));
            case PRICE_HIGH_TO_LOW -> query.orderBy(cb.desc(activePrice), cb.desc(root.get("id")));
            case NEWEST -> query.orderBy(cb.desc(root.get("createdDate")), cb.desc(root.get("id")));
        }
    }
}
