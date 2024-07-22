package product_service.service;

import org.springframework.web.multipart.MultipartFile;
import product_service.dto.product.ProductAddDto;
import product_service.dto.product.ProductAdminDto;
import product_service.dto.product.ProductUpdateDto;

import java.util.List;

public interface ProductService {
    public List<ProductAdminDto> getAllProductAdmin();
    public ProductAdminDto addProduct(ProductAddDto productAddDto);
    public ProductAdminDto updateProduct(Long id, ProductUpdateDto productUpdateDto);
    public ProductAdminDto deleteProduct(Long id);
    public ProductAdminDto uploadImageById(Long id, MultipartFile file);
}
