package product_service.service.impl;

import org.springframework.stereotype.Service;
import product_service.dto.revenue.RevenueProduct;
import product_service.dto.revenue.RevenueRequest;
import product_service.dto.revenue.RevenueResponse;
import product_service.dto.statistics.StatisticsResponse;
import product_service.exception.NotFoundException;
import product_service.model.Product;
import product_service.repository.OrderRepository;
import product_service.repository.ProductRepository;
import product_service.repository.PromotionRepository;
import product_service.repository.PurchaseOrderRepository;
import product_service.service.StatisticsService;
import product_service.service.client.PeopleFeignClient;
import product_service.utils.Constants;

import java.util.ArrayList;
import java.util.List;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PeopleFeignClient peopleFeignClient;
    private final PromotionRepository promotionRepository;

    public StatisticsServiceImpl(ProductRepository productRepository, OrderRepository orderRepository, PurchaseOrderRepository purchaseOrderRepository, PeopleFeignClient peopleFeignClient, PromotionRepository promotionRepository) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.peopleFeignClient = peopleFeignClient;
        this.promotionRepository = promotionRepository;
    }

    public RevenueResponse findTop5ProductsByRevenue(RevenueRequest revenueRequest) {
        Long revenueTotal = orderRepository.findRevenueByMonthAndYear(revenueRequest.year(), revenueRequest.month(), revenueRequest.smallTraderId());
        List<Object[]> products = orderRepository.findTop5ProductsByRevenue(revenueRequest.year(), revenueRequest.month(), revenueRequest.smallTraderId());
        List<RevenueProduct> list = new ArrayList<>();
        for (Object[] item : products) {
            Long id = (Long) item[0];
            Long revenue = (Long) item[1];
            Product product = productRepository.findById(id).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, id)));
            String productName = product.getName();
            RevenueProduct revenueProduct = new RevenueProduct(
                    id,
                    productName,
                    revenue
            );
            list.add(revenueProduct);
            if (list.size() == 5) {
                break;
            }
        }

        Long moneyPurchase = purchaseOrderRepository.findMoneyPurchaseByMonthAndYear(revenueRequest.year(), revenueRequest.month(), revenueRequest.smallTraderId());
        return new RevenueResponse(
                revenueRequest.month(),
                revenueRequest.year(),
                revenueTotal,
                moneyPurchase,
                list
        );
    }
    public StatisticsResponse getDataHomePage(RevenueRequest revenueRequest) {
        Long purchaseOrderNumber = purchaseOrderRepository
                .countPurchaseOrdersByStatusAndMonth(revenueRequest.year(),
                        revenueRequest.month(),
                        revenueRequest.smallTraderId());
        Long productNumber = productRepository.countProductsByStatusTrue(revenueRequest.smallTraderId());
        Long orderNumber = orderRepository.countOrdersByStatusAndMonth(revenueRequest.year(), revenueRequest.month(), revenueRequest.smallTraderId());
        Long customerNumber = countCustomer(revenueRequest.smallTraderId());
        Long supplierNumber = countSupplier(revenueRequest.smallTraderId());
        Long promotionNumber = promotionRepository.countPromotionsByStatusAndMonth(revenueRequest.year(), revenueRequest.month(), revenueRequest.smallTraderId());
        return new StatisticsResponse(
                productNumber,
                orderNumber,
                customerNumber,
                purchaseOrderNumber,
                supplierNumber,
                promotionNumber
        );
    }

    private Long countCustomer(Long id) {
        Long customerNumber = peopleFeignClient.countCustomerBySmallTraderId(id).getBody();
        return customerNumber;
    }

    private Long countSupplier(Long id) {
        Long supplierNumber = peopleFeignClient.countSupplierBySmallTraderId(id).getBody();
        return supplierNumber;
    }

}
