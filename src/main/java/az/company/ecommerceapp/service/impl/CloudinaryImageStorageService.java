package az.company.ecommerceapp.service.impl;

import az.company.ecommerceapp.config.CloudinaryProperties;
import az.company.ecommerceapp.exception.FileUploadException;
import az.company.ecommerceapp.service.ImageStorageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryImageStorageService implements ImageStorageService {

    private static final Set<String> ALLOWED_TYPES =
            Set.of("image/jpeg", "image/png", "image/webp", "image/gif", "image/jpg");
    private static final long MAX_BYTES = 5 * 1024 * 1024; // 5 MB

    private final Cloudinary cloudinary;
    private final CloudinaryProperties props;

    @Override
    public UploadResult upload(MultipartFile file) {
        validate(file);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", props.uploadFolder(),
                            "resource_type", "image",
                            "quality", "auto",
                            "fetch_format", "auto"
                    )
            );
            String url = (String) result.get("secure_url");
            String publicId = (String) result.get("public_id");
            log.info("Uploaded image to Cloudinary: {}", publicId);
            return new UploadResult(url, publicId);
        } catch (IOException e) {
            log.error("Cloudinary upload failed", e);
            throw new FileUploadException("Failed to upload image");
        }
    }

    @Override
    public void delete(String publicId) {
        if (!StringUtils.hasText(publicId)) {
            return;
        }
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Deleted image from Cloudinary: {}", publicId);
        } catch (IOException e) {
            log.warn("Failed to delete Cloudinary image: {}", publicId, e);
        }
    }

    private void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException("Image file is required");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new FileUploadException("Image exceeds the 5 MB size limit");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new FileUploadException("Unsupported image type: " + file.getContentType());
        }
    }
}
