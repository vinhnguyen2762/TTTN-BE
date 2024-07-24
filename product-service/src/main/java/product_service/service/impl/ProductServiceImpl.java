package product_service.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import product_service.dto.product.ProductAddDto;
import product_service.dto.product.ProductAdminDto;
import product_service.dto.product.ProductSearchDto;
import product_service.dto.product.ProductUpdateDto;
import product_service.exception.DuplicateException;
import product_service.exception.NotFoundException;
import product_service.model.Product;
import product_service.repository.ProductRepository;
import product_service.service.CloudinaryService;
import product_service.service.ProductService;
import product_service.utils.Constants;

import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;

    public ProductServiceImpl(ProductRepository productRepository, CloudinaryService cloudinaryService) {
        this.productRepository = productRepository;
        this.cloudinaryService = cloudinaryService;
    }

    public List<ProductAdminDto> getAllProductAdmin() {
        List<Product> list = productRepository.findAll();
        return list.stream().map(ProductAdminDto::fromProduct).toList();
    }

    public ProductAdminDto addProduct(ProductAddDto productAddDto) {
        Boolean isExist = productRepository.findByName(productAddDto.name()).isPresent();
        if (isExist) {
            throw new DuplicateException(String.format(Constants.ErrorMessage.PRODUCT_ALREADY_TAKEN, productAddDto.name()));
        }

        Long price = Long.parseLong(productAddDto.price());
        Integer quantity = Integer.parseInt(productAddDto.quantity());

        Product productAdd = new Product(
                productAddDto.name(),
                productAddDto.description(),
                price,
                quantity);
        productRepository.saveAndFlush(productAdd);
        return ProductAdminDto.fromProduct(productAdd);
    }

    public ProductAdminDto updateProduct(Long id, ProductUpdateDto productUpdateDto) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, id)));
        if (product.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, id));
        }

        Long price = Long.parseLong(productUpdateDto.price());
        Integer quantity = Integer.parseInt(productUpdateDto.quantity());

        product.setDescription(productUpdateDto.description());
        product.setPrice(price);
        product.setQuantity(quantity);
        productRepository.saveAndFlush(product);
        return ProductAdminDto.fromProduct(product);
    }

    public ProductAdminDto deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, id)));
        if (product.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, id));
        }
        product.setStatus(false);
        productRepository.saveAndFlush(product);
        return ProductAdminDto.fromProduct(product);
    }

    public ProductAdminDto uploadImageById(Long id, MultipartFile file) {
        Map<String, Object> result = cloudinaryService.uploadFile(file);
        String imagePath = (String) result.get("url");

        Product product = productRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, id)));
        if (product.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, id));
        }

        product.setImagePath(imagePath);
        productRepository.saveAndFlush(product);
        return ProductAdminDto.fromProduct(product);
    }

    public ProductSearchDto findByName(String name) {
        Product product = productRepository.findByName(name).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND_NAME, name)));
        if (product.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND_NAME, name));
        }

        return new ProductSearchDto(
                product.getPrice().toString(),
                product.getQuantity().toString()
        );
    }
}
