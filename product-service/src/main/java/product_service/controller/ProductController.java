package product_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import product_service.dto.product.ProductAddDto;
import product_service.dto.product.ProductAdminDto;
import product_service.dto.product.ProductSearchDto;
import product_service.dto.product.ProductUpdateDto;
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

    @PostMapping("/add")
    public ResponseEntity<ProductAdminDto> addProduct(@RequestBody ProductAddDto productAddDto) {
        ProductAdminDto productAdminDto = productService.addProduct(productAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productAdminDto);
    }

    @PutMapping ("/update/{id}")
    public ResponseEntity<ProductAdminDto> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateDto productUpdateDto) {
        ProductAdminDto productAdminDto = productService.updateProduct(id, productUpdateDto);
        return ResponseEntity.ok().body(productAdminDto);
    }

    @DeleteMapping ("/delete/{id}")
    public ResponseEntity<ProductAdminDto> deleteProduct(@PathVariable Long id) {
        ProductAdminDto productAdminDto = productService.deleteProduct(id);
        return ResponseEntity.ok().body(productAdminDto);
    }

    @PostMapping("/upload-file/{id}")
    public ResponseEntity<ProductAdminDto> uploadFileByProductId(@PathVariable("id") Long id, @RequestParam("image") MultipartFile file) {
        ProductAdminDto productAdminDto = productService.uploadImageById(id, file);
        return ResponseEntity.ok().body(productAdminDto);
    }

    @GetMapping ("/search")
    public ResponseEntity<ProductSearchDto> searchByName(@RequestParam("productName") String name) {
        ProductSearchDto productSearchDto = productService.findByName(name);
        return ResponseEntity.ok().body(productSearchDto);
    }
}
