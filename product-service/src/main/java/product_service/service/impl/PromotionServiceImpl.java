package product_service.service.impl;

import org.springframework.stereotype.Service;
import product_service.dto.smallTrader.SmallTraderAdminDto;
import product_service.dto.promotion.PromotionAddDto;
import product_service.dto.promotion.PromotionAdminDto;
import product_service.dto.promotion.PromotionPostDto;
import product_service.dto.promotionDetail.PromotionDetailAdminDto;
import product_service.dto.promotionDetail.PromotionDetailPostDto;
import product_service.enums.PromotionStatus;
import product_service.enums.PromotionType;
import product_service.exception.DuplicateException;
import product_service.exception.FailedException;
import product_service.exception.NotFoundException;
import product_service.model.Product;
import product_service.model.Promotion;
import product_service.model.PromotionDetail;
import product_service.repository.ProductRepository;
import product_service.repository.PromotionDetailRepository;
import product_service.repository.PromotionRepository;
import product_service.service.PromotionService;
import product_service.service.client.PeopleFeignClient;
import product_service.utils.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final ProductRepository productRepository;
    private final PromotionDetailRepository promotionDetailRepository;
    private final PeopleFeignClient peopleFeignClient;

    public PromotionServiceImpl(PromotionRepository promotionRepository, ProductRepository productRepository, PromotionDetailRepository promotionDetailRepository, PeopleFeignClient peopleFeignClient) {
        this.promotionRepository = promotionRepository;
        this.productRepository = productRepository;
        this.promotionDetailRepository = promotionDetailRepository;
        this.peopleFeignClient = peopleFeignClient;
    }

    public List<PromotionAdminDto> getAllPromotionAdmin() {
        List<Promotion> promotionList = promotionRepository.findAll();
        return promotionList.stream().map(p -> {
            List<PromotionDetailAdminDto> list = p.getPromotionDetailList().stream().map(pd -> {
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
                return PromotionDetailAdminDto.fromPromotionProduct(pd, productName, originalPrice.toString(), discountPrice.toString());
            }).toList();
            return PromotionAdminDto.fromPromotion(p, list);
        }).toList();
    }

    public Long addPromotion(PromotionAddDto promotionAddDto) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = LocalDate.parse(promotionAddDto.startDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate endDate = LocalDate.parse(promotionAddDto.endDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        if (startDate.isBefore(today)) {
            throw new FailedException("Start date is before today");
        }  else if (startDate.isAfter(endDate)) {
            throw new FailedException(("Start date is after end date"));
        }

        PromotionType type = promotionAddDto.type().equals("Phần trăm") ? PromotionType.PERCENTAGE : PromotionType.FIXED_AMOUNT;
        Long value = Long.parseLong(promotionAddDto.value());

        Promotion promotion = new Promotion(
                promotionAddDto.name(),
                promotionAddDto.description(),
                type,
                value,
                startDate,
                endDate,
                promotionAddDto.smallTraderId()
        );
        promotionRepository.saveAndFlush(promotion);

        List<PromotionDetail> promotionDetailList = new ArrayList<>();
        for (PromotionDetailPostDto item : promotionAddDto.list()) {

            PromotionDetail promotionDetail = new PromotionDetail(
                    item.productId(),
                    promotion
            );
            promotionDetailList.add(promotionDetail);
        }
        promotionDetailRepository.saveAll(promotionDetailList);

        return promotion.getId();
    }

    public Long updatePromotion(Long id, PromotionPostDto promotionPostDto) {
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

        List<PromotionDetail> updateList = promotion.getPromotionDetailList();
        updateList.clear();

        for (PromotionDetailPostDto item : promotionPostDto.list()) {

            PromotionDetail promotionDetail = new PromotionDetail(
                    item.productId(),
                    promotion
            );
            updateList.add(promotionDetail);
        }
        promotionRepository.save(promotion);

        return promotion.getId();
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
        LocalDate today = LocalDate.now();
        Promotion promotion = promotionRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.PROMOTION_NOT_FOUND, id)));
        if (promotion.getStatus().name().equals("DELETED")) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PROMOTION_NOT_FOUND, id));
        }
        if (promotion.getStatus().name().equals("ACTIVE")) {
            promotion.setStatus(PromotionStatus.PENDING);
            promotionRepository.saveAndFlush(promotion);
            return id;
        }

        List<PromotionDetail> promotionDetailList = promotion.getPromotionDetailList();
        Boolean canActive = true;
        for (PromotionDetail pd : promotionDetailList) {
            List<PromotionDetail> listFound = promotionDetailRepository.findByProductId(pd.getProductId());
            for (PromotionDetail pd1 : listFound) {
                Promotion promotionCheck = promotionRepository.findById(pd1.getPromotion().getId()).orElseThrow(
                        () -> new NotFoundException(String.format(Constants.ErrorMessage.PROMOTION_NOT_FOUND, id)));
                if (promotionCheck.getStatus().name().equals("ACTIVE") && !promotionCheck.getEndDate().isBefore(today)) {
                    canActive = false;
                }
            }
        }
        if (canActive) {
            promotion.setStatus(PromotionStatus.ACTIVE);
            promotionRepository.saveAndFlush(promotion);
        } else {
            throw new DuplicateException(String.format(Constants.ErrorMessage.PRODUCT_ALREADY_HAS_PROMOTION));
        }
        return promotion.getId();
    }

    public List<PromotionAdminDto> getAllPromotionSmallTrader(Long id) {
        List<Promotion> promotionList = promotionRepository.findBySmallTraderId(id);
        return promotionList.stream().map(p -> {
            List<PromotionDetailAdminDto> list = p.getPromotionDetailList().stream().map(pd -> {
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
                return PromotionDetailAdminDto.fromPromotionProduct(pd, productName, originalPrice.toString(), discountPrice.toString());
            }).toList();
            return PromotionAdminDto.fromPromotion(p, list);
        }).toList();
    }

    private SmallTraderAdminDto findSmallTraderById(Long id) {
        SmallTraderAdminDto smallTraderAdminDto = peopleFeignClient.getById(id).getBody();
        return smallTraderAdminDto;
    }
}
