package az.company.ecommerceapp.service;

public interface EmailService {

    void sendVerificationCodeEmail(String to, String token);

    void sendPasswordResetCodeEmail(String to, String token);
}