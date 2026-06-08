package az.company.ecommerceapp.util;

import java.text.Normalizer;
import java.util.Locale;

public final class SlugUtils {

    private SlugUtils() {
    }

    public static String from(String value) {
        if (value == null) {
            return null;
        }

        String normalized = normalizeAzerbaijani(value.trim().toLowerCase(Locale.ROOT));
        String ascii = Normalizer.normalize(normalized, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");

        return ascii
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
    }

    private static String normalizeAzerbaijani(String value) {
        StringBuilder builder = new StringBuilder(value.length());

        for (char ch : value.toCharArray()) {
            switch (ch) {
                case '\u0259', '\u018f' -> builder.append('e');
                case '\u0131', '\u0130' -> builder.append('i');
                case '\u011f', '\u011e' -> builder.append('g');
                case '\u00f6', '\u00d6' -> builder.append('o');
                case '\u00fc', '\u00dc' -> builder.append('u');
                case '\u015f', '\u015e' -> builder.append('s');
                case '\u00e7', '\u00c7' -> builder.append('c');
                default -> builder.append(ch);
            }
        }

        return builder.toString();
    }
}
