package az.company.ecommerceapp.model.enums;

import java.util.Locale;

public enum ProductSortOption {

    NEWEST,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW;

    public static ProductSortOption from(String value) {
        if (value == null || value.isBlank()) {
            return NEWEST;
        }

        String normalized = value.trim()
                .toLowerCase(Locale.ROOT)
                .replace(":", "")
                .replace(",", "")
                .replace("_", "-")
                .replace(" ", "-");

        return switch (normalized) {
            case "newest", "new", "yenilik" -> NEWEST;
            case "price-low-to-high", "price-asc", "low-to-high", "asc" -> PRICE_LOW_TO_HIGH;
            case "price-high-to-low", "price-desc", "high-to-low", "desc" -> PRICE_HIGH_TO_LOW;
            default -> throw new IllegalArgumentException("Unsupported product sort: " + value);
        };
    }
}
