package product_service.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import product_service.dto.smallTrader.SmallTraderAdminDto;
import product_service.dto.product.*;
import product_service.exception.DuplicateException;
import product_service.exception.FailedException;
import product_service.exception.NotFoundException;
import product_service.model.Product;
import product_service.model.Promotion;
import product_service.model.PromotionDetail;
import product_service.repository.ProductRepository;
import product_service.repository.PromotionDetailRepository;
import product_service.repository.PromotionRepository;
import product_service.service.CloudinaryService;
import product_service.service.ProductService;
import product_service.service.client.PeopleFeignClient;
import product_service.utils.Constants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CloudinaryService cloudinaryService;
    private final PromotionDetailRepository promotionDetailRepository;
    private final PromotionRepository promotionRepository;
    private final PeopleFeignClient peopleFeignClient;

    public ProductServiceImpl(ProductRepository productRepository, CloudinaryService cloudinaryService, PromotionDetailRepository promotionDetailRepository, PromotionRepository promotionRepository, PeopleFeignClient peopleFeignClient) {
        this.productRepository = productRepository;
        this.cloudinaryService = cloudinaryService;
        this.promotionDetailRepository = promotionDetailRepository;
        this.promotionRepository = promotionRepository;
        this.peopleFeignClient = peopleFeignClient;
    }

    public List<ProductAdminDto> getAllProductAdmin() {
        LocalDate today = LocalDate.now();
        List<Product> list = productRepository.findAll();
        List<PromotionDetail> promotionDetailList = promotionDetailRepository.findAll();
        return list.stream().map(p -> {
            for (PromotionDetail pd : promotionDetailList) {;
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
        List<PromotionDetail> promotionDetailList = promotionDetailRepository.findAll();
        for (Product p : list) {
            Boolean noContain = false;
            for (PromotionDetail pd : promotionDetailList) {
                if (pd.getProductId() == p.getId()) {
                    Promotion promotion = promotionRepository.findById(pd.getPromotion().getId()).orElseThrow(
                            () -> new NotFoundException(String.format(Constants.ErrorMessage.PROMOTION_NOT_FOUND, pd.getPromotion().getId())));
                    if (!promotion.getStatus().name().equals("DELETED") && !promotion.getEndDate().isBefore(today)) {
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

    public List<ProductNoPromotionDto> getAllProductNoPromotionSmallTrader(Long id) {
        LocalDate today = LocalDate.now();
        List<Product> list = productRepository.findBySmallTraderId(id);
        List<Product> result = new ArrayList<>();
        List<PromotionDetail> promotionDetailList = promotionDetailRepository.findAll();
        for (Product p : list) {
            Boolean noContain = false;
            for (PromotionDetail pd : promotionDetailList) {
                if (pd.getProductId() == p.getId()) {
                    Promotion promotion = promotionRepository.findById(pd.getPromotion().getId()).orElseThrow(
                            () -> new NotFoundException(String.format(Constants.ErrorMessage.PROMOTION_NOT_FOUND, pd.getPromotion().getId())));
                    if (!promotion.getStatus().name().equals("DELETED") && !promotion.getEndDate().isBefore(today)) {
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
        Boolean isExist = productRepository.findByNameSmallTraderId(productAddDto.smallTraderId(), productAddDto.name()).isPresent();
        if (isExist) {
            throw new DuplicateException(String.format(Constants.ErrorMessage.PRODUCT_ALREADY_TAKEN, productAddDto.name()));
        }

        Long price = Long.parseLong(productAddDto.price());
        Integer quantity = Integer.parseInt(productAddDto.quantity());

        Product productAdd = new Product(
                productAddDto.name(),
                productAddDto.description(),
                price,
                quantity,
                productAddDto.smallTraderId());
        productRepository.saveAndFlush(productAdd);
        return productAdd.getId();
    }

    public Long updateProduct(Long id, ProductAddDto productAddDto) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, id)));
        if (product.getStatus() == false) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, id));
        }

        String oldName = product.getName();

        // if name is new, check if name exist
        if (!productAddDto.name().equals(oldName)) {
            Boolean isNameExist = productRepository.findByNameSmallTraderId(productAddDto.smallTraderId(), productAddDto.name()).isPresent();
            if (!isNameExist) {
                product.setName(productAddDto.name());
            } else {
                throw new DuplicateException(String.format(Constants.ErrorMessage.PRODUCT_ALREADY_TAKEN, productAddDto.name()));
            }
        }

        Long price = Long.parseLong(productAddDto.price());
        Integer quantity = Integer.parseInt(productAddDto.quantity());

        product.setDescription(productAddDto.description());
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

    public List<ProductAdminDto> getAllProductSmallTrader(Long id) {
        LocalDate today = LocalDate.now();
        List<Product> list = productRepository.findBySmallTraderId(id);
        List<PromotionDetail> promotionDetailList = promotionDetailRepository.findAll();
        return list.stream().map(p -> {
            for (PromotionDetail pd : promotionDetailList) {;
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

    private SmallTraderAdminDto findSmallTraderById(Long id) {
        SmallTraderAdminDto smallTraderAdminDto = peopleFeignClient.getById(id).getBody();
        return smallTraderAdminDto;
    }
}
