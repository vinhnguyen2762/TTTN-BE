package product_service.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudinaryService {
    public Map<String, Object> uploadFile(MultipartFile file);
}
