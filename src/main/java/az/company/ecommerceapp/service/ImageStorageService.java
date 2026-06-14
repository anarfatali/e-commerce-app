package az.company.ecommerceapp.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {

    UploadResult upload(MultipartFile file);

    void delete(String publicId);

    record UploadResult(String url, String publicId) {}
}