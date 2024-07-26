package product_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import product_service.dto.product.*;
import product_service.service.CloudinaryService;
import product_service.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product")
public class ProductController {

    private final ProductService productService;
    private final CloudinaryService cloudinaryService;

    public ProductController(ProductService productService, CloudinaryService cloudinaryService) {
        this.productService = productService;
        this.cloudinaryService = cloudinaryService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<ProductAdminDto>> getAllProductAdmin() {
        List<ProductAdminDto> list = productService.getAllProductAdmin();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/get-all-no-promotion")
    public ResponseEntity<List<ProductNoPromotionDto>> getAllNoPromotionProduct() {
        List<ProductNoPromotionDto> list = productService.getAllProductNoPromotion();
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/add")
    public ResponseEntity<Long> addProduct(@RequestBody ProductAddDto productAddDto) {
        Long rs = productService.addProduct(productAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(rs);
    }

    @PutMapping ("/update/{id}")
    public ResponseEntity<Long> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateDto productUpdateDto) {
        Long rs = productService.updateProduct(id, productUpdateDto);
        return ResponseEntity.ok().body(rs);
    }

    @DeleteMapping ("/delete/{id}")
    public ResponseEntity<Long> deleteProduct(@PathVariable Long id) {
        Long rs = productService.deleteProduct(id);
        return ResponseEntity.ok().body(rs);
    }

    @PostMapping("/upload-file/{id}")
    public ResponseEntity<Long> uploadFileByProductId(@PathVariable("id") Long id, @RequestParam("image") MultipartFile file) {
        Long rs = productService.uploadImageById(id, file);
        return ResponseEntity.ok().body(rs);
    }

    @GetMapping ("/search")
    public ResponseEntity<ProductSearchDto> searchByName(@RequestParam("productName") String name) {
        ProductSearchDto productSearchDto = productService.findByName(name);
        return ResponseEntity.ok().body(productSearchDto);
    }
}
