package az.company.ecommerceapp.exception;

public class DuplicateSlugException extends RuntimeException {
    public DuplicateSlugException(String message) {
        super(message);
    }
}
