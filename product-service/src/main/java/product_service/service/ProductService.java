package product_service.service;

import org.springframework.web.multipart.MultipartFile;
import product_service.dto.product.*;

import java.util.List;

public interface ProductService {
    public List<ProductAdminDto> getAllProductAdmin();
    public List<ProductNoPromotionDto> getAllProductNoPromotion();
    public List<ProductNoPromotionDto> getAllProductNoPromotionSmallTrader(Long id);
    public Long addProduct(ProductAddDto productAddDto);
    public Long updateProduct(Long id, ProductUpdateDto productUpdateDto);
    public Long deleteProduct(Long id);
    public Long uploadImageById(Long id, MultipartFile file);
    public ProductSearchDto findByName(String name);
    public List<ProductAdminDto> getAllProductSmallTrader(Long id);

}
