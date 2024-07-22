package product_service.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import product_service.exception.FailedException;
import product_service.service.CloudinaryService;
import product_service.utils.Constants;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public Map<String, Object> uploadFile(MultipartFile file) {
        try{
            Map<String, Object> params = ObjectUtils.asMap(
                    "public_id", getFileNameWithoutExtension(file.getOriginalFilename())
            );
            Map data = cloudinary.uploader().upload(file.getBytes(), params);
            String url = (String) data.get("url");
            return Map.of("url", url);
        } catch (IOException io) {
            throw new FailedException(String.format(Constants.ErrorMessage.IMAGE_UPLOAD_FAIL, file.getOriginalFilename()));
        }
    }

    private String getFileNameWithoutExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return fileName;
        }
        int lastIndexOfDot = fileName.lastIndexOf('.');
        if (lastIndexOfDot == -1) {
            return fileName; // Không có phần mở rộng
        }
        return fileName.substring(0, lastIndexOfDot);
    }
}
