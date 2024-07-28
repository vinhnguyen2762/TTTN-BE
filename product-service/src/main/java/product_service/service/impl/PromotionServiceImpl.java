package product_service.service.impl;

import org.springframework.stereotype.Service;
import product_service.dto.promotion.PromotionAdminDto;
import product_service.dto.promotion.PromotionPostDto;
import product_service.dto.promotionProduct.PromotionProductAdminDto;
import product_service.dto.promotionProduct.PromotionProductPostDto;
import product_service.enums.PromotionStatus;
import product_service.enums.PromotionType;
import product_service.exception.FailedException;
import product_service.exception.NotFoundException;
import product_service.model.Product;
import product_service.model.Promotion;
import product_service.model.PromotionProduct;
import product_service.repository.ProductRepository;
import product_service.repository.PromotionProductRepository;
import product_service.repository.PromotionRepository;
import product_service.service.PromotionService;
import product_service.utils.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;
    private final PromotionProductRepository promotionProductRepository;

    public PromotionServiceImpl(PromotionRepository promotionRepository, ProductRepository productRepository, PromotionProductRepository promotionProductRepository) {
        this.promotionRepository = promotionRepository;
        this.productRepository = productRepository;
        this.promotionProductRepository = promotionProductRepository;
    }

    public List<PromotionAdminDto> getAllPromotionAdmin() {
        List<Promotion> promotionList = promotionRepository.findAll();
        return promotionList.stream().map(p -> {
            List<PromotionProductAdminDto> list = p.getPromotionProductList().stream().map(pd -> {
                Product product = productRepository.findById(pd.getProductId()).orElseThrow(
                        () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, pd.getProductId())));
                if (product.getStatus() == false) {
                    throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, pd.getProductId()));
                }
                String productName = product.getName();
                Long originalPrice = product.getPrice();
                Long discountPrice;
                if (p.getType().name().equals("PERCENTAGE")) {
                    discountPrice = originalPrice -  (originalPrice * p.getValue()/100);
                } else {
                    discountPrice = originalPrice - p.getValue();
                }
                return PromotionProductAdminDto.fromPromotionProduct(pd, productName, originalPrice.toString(), discountPrice.toString());
            }).toList();
            return PromotionAdminDto.fromPromotion(p, list);
        }).toList();
    }

    public PromotionAdminDto addPromotion(PromotionPostDto promotionPostDto) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = LocalDate.parse(promotionPostDto.startDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate endDate = LocalDate.parse(promotionPostDto.endDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        if (startDate.isBefore(today)) {
            throw new FailedException("Start date is before today");
        }  else if (startDate.isAfter(endDate)) {
            throw new FailedException(("Start date is after end date"));
        }

        PromotionType type = promotionPostDto.type().equals("Phần trăm") ? PromotionType.PERCENTAGE : PromotionType.FIXED_AMOUNT;
        Long value = Long.parseLong(promotionPostDto.value());

        Promotion promotion = new Promotion(
                promotionPostDto.name(),
                promotionPostDto.description(),
                type,
                value,
                startDate,
                endDate
        );
        promotionRepository.saveAndFlush(promotion);

        List<PromotionProduct> promotionProductList = new ArrayList<>();
        for (PromotionProductPostDto item : promotionPostDto.list()) {

            PromotionProduct promotionProduct = new PromotionProduct(
                    item.productId(),
                    promotion
            );
            promotionProductList.add(promotionProduct);
        }
        promotionProductRepository.saveAll(promotionProductList);

        List<PromotionProductAdminDto> list = promotionProductList.stream().map(pd -> {
            Product product = productRepository.findById(pd.getProductId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, pd.getProductId())));
            if (product.getStatus() == false) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, pd.getProductId()));
            }
            String productName = product.getName();
            Long originalPrice = product.getPrice();
            Long discountPrice;
            if (promotion.getType().name().equals("PERCENTAGE")) {
                discountPrice = originalPrice -  (originalPrice * promotion.getValue()/100);
            } else {
                discountPrice = originalPrice - promotion.getValue();
                if (discountPrice < 0) {
                    throw new FailedException("Discount price is more than original price");
                }
            }
            return PromotionProductAdminDto.fromPromotionProduct(pd, productName, originalPrice.toString(), discountPrice.toString());
        }).toList();

        return PromotionAdminDto.fromPromotion(promotion, list);
    }

    public PromotionAdminDto updatePromotion(Long id, PromotionPostDto promotionPostDto) {
        Promotion promotion = promotionRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.PROMOTION_NOT_FOUND, id)));
        if (promotion.getStatus().name().equals("DELETED")) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PROMOTION_NOT_FOUND, id));
        }

        PromotionType type = promotionPostDto.type().equals("Phần trăm") ? PromotionType.PERCENTAGE : PromotionType.FIXED_AMOUNT;
        Long value = Long.parseLong(promotionPostDto.value());

        LocalDate startDate = LocalDate.parse(promotionPostDto.startDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate endDate = LocalDate.parse(promotionPostDto.endDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        if (startDate.isAfter(endDate)) {
            throw new FailedException(("Start date is after end date"));
        }

        promotion.setName(promotionPostDto.name());
        promotion.setDescription(promotionPostDto.description());
        promotion.setType(type);
        promotion.setValue(value);
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);

        List<PromotionProduct> updateList = promotion.getPromotionProductList();
        updateList.clear();

        for (PromotionProductPostDto item : promotionPostDto.list()) {

            PromotionProduct promotionProduct = new PromotionProduct(
                    item.productId(),
                    promotion
            );
            updateList.add(promotionProduct);
        }
        promotionRepository.save(promotion);

        List<PromotionProductAdminDto> list = updateList.stream().map(pd -> {
            Product product = productRepository.findById(pd.getProductId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, pd.getProductId())));
            if (product.getStatus() == false) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, pd.getProductId()));
            }
            String productName = product.getName();
            Long originalPrice = product.getPrice();
            Long discountPrice;
            if (promotion.getType().name().equals("PERCENTAGE")) {
                discountPrice = originalPrice - (originalPrice * promotion.getValue()/100);
            } else {
                discountPrice = originalPrice - promotion.getValue();
                if (discountPrice < 0) {
                    throw new FailedException("Discount price is more than original price");
                }
            }
            return PromotionProductAdminDto.fromPromotionProduct(pd, productName, originalPrice.toString(), discountPrice.toString());
        }).toList();

        return PromotionAdminDto.fromPromotion(promotion, list);
    }

    public Long deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.PROMOTION_NOT_FOUND, id)));
        if (promotion.getStatus().name().equals("DELETED")) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PROMOTION_NOT_FOUND, id));
        }
        promotion.setStatus(PromotionStatus.DELETED);
        promotionRepository.saveAndFlush(promotion);
        return id;
    }

    public Long activePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.PROMOTION_NOT_FOUND, id)));
        if (promotion.getStatus().name().equals("DELETED")) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PROMOTION_NOT_FOUND, id));
        } else if (promotion.getStatus().name().equals("ACTIVE")) {
            throw new FailedException(String.format(Constants.ErrorMessage.PROMOTION_ALREADY_ACTIVE, id));
        }
        promotion.setStatus(PromotionStatus.ACTIVE);
        promotionRepository.saveAndFlush(promotion);
        return id;
    }
}
