package product_service.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import product_service.dto.product.*;
import product_service.exception.DuplicateException;
import product_service.exception.NotFoundException;
import product_service.model.Product;
import product_service.model.Promotion;
import product_service.model.PromotionProduct;
import product_service.repository.ProductRepository;
import product_service.repository.PromotionProductRepository;
import product_service.repository.PromotionRepository;
import product_service.service.CloudinaryService;
import product_service.service.ProductService;
import product_service.utils.Constants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    private final PromotionProductRepository promotionProductRepository;
    private final PromotionRepository promotionRepository;

    public ProductServiceImpl(ProductRepository productRepository, CloudinaryService cloudinaryService, PromotionProductRepository promotionProductRepository, PromotionRepository promotionRepository) {
        this.productRepository = productRepository;
        this.cloudinaryService = cloudinaryService;
        this.promotionProductRepository = promotionProductRepository;
        this.promotionRepository = promotionRepository;
    }

    public List<ProductAdminDto> getAllProductAdmin() {
        LocalDate today = LocalDate.now();
        List<Product> list = productRepository.findAll();
        List<PromotionProduct> promotionProductList = promotionProductRepository.findAll();
        return list.stream().map(p -> {
            for (PromotionProduct pd : promotionProductList) {;
                if (pd.getProductId() == p.getId()) {
                    Promotion promotion = promotionRepository.findById(pd.getPromotion().getId()).orElseThrow(
                            () -> new NotFoundException(String.format(Constants.ErrorMessage.PROMOTION_NOT_FOUND, pd.getPromotion().getId())));
                    if (promotion.getStatus().name().equals("ACTIVE") && !promotion.getEndDate().isBefore(today)) {
                        String promotionName = promotion.getName();
                        Long originalPrice = p.getPrice();
                        Long discountedPrice;
                        if (promotion.getType().name().equals("PERCENTAGE")) {
                            discountedPrice = originalPrice -  (originalPrice * promotion.getValue()/100);
                        } else {
                            discountedPrice = originalPrice - promotion.getValue();
                        }
                        return ProductAdminDto.fromProduct(p, promotionName, discountedPrice.toString());
                    }
                }
            }
            return ProductAdminDto.fromProduct(p, "Không", p.getPrice().toString());
        }).toList();
    }

    public List<ProductNoPromotionDto> getAllProductNoPromotion() {
        LocalDate today = LocalDate.now();
        List<Product> list = productRepository.findAll();
        List<Product> result = new ArrayList<>();
        List<PromotionProduct> promotionProductList = promotionProductRepository.findAll();
        for (Product p : list) {
            Boolean noContain = false;
            for (PromotionProduct pd : promotionProductList) {
                if (pd.getProductId() == p.getId()) {
                    Promotion promotion = promotionRepository.findById(pd.getPromotion().getId()).orElseThrow(
                            () -> new NotFoundException(String.format(Constants.ErrorMessage.PROMOTION_NOT_FOUND, pd.getPromotion().getId())));
                    if (promotion.getStatus().name().equals("ACTIVE") && !promotion.getEndDate().isBefore(today)) {
                        noContain = true;
                    }
                }
            }
            if (noContain == false) {
                result.add(p);
            }
        }
        return result.stream().map(p -> new ProductNoPromotionDto(p.getId(), p.getName())).toList();
    }

    public Long addProduct(ProductAddDto productAddDto) {
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
        return productAdd.getId();
    }

    public Long updateProduct(Long id, ProductUpdateDto productUpdateDto) {
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
        return product.getId();
    }

    public Long deleteProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, id)));
        if (product.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, id));
        }
        product.setStatus(false);
        productRepository.saveAndFlush(product);
        return product.getId();
    }

    public Long uploadImageById(Long id, MultipartFile file) {
        Map<String, Object> result = cloudinaryService.uploadFile(file);
        String imagePath = (String) result.get("url");

        Product product = productRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, id)));
        if (product.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, id));
        }

        product.setImagePath(imagePath);
        productRepository.saveAndFlush(product);
        return product.getId();
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
