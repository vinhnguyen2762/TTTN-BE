package product_service.service.impl;

import org.springframework.stereotype.Service;
import product_service.dto.employee.EmployeeAdminDto;
import product_service.dto.revenue.RevenueProduct;
import product_service.dto.revenue.RevenueRequest;
import product_service.dto.revenue.RevenueResponse;
import product_service.dto.statistics.StatisticsResponse;
import product_service.exception.NotFoundException;
import product_service.model.Product;
import product_service.repository.OrderRepository;
import product_service.repository.ProductRepository;
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

    public StatisticsServiceImpl(ProductRepository productRepository, OrderRepository orderRepository, PurchaseOrderRepository purchaseOrderRepository, PeopleFeignClient peopleFeignClient) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.peopleFeignClient = peopleFeignClient;
    }

    public RevenueResponse findTop5ProductsByRevenue(RevenueRequest revenueRequest) {
        Long revenueTotal = orderRepository.findRevenueByMonthAndYear(revenueRequest.year(), revenueRequest.month());
        List<Object[]> products = orderRepository.findTop5ProductsByRevenue(revenueRequest.year(), revenueRequest.month());
        List<RevenueProduct> list = new ArrayList<>();
        for (Object[] item : products) {
            Long id = (Long) item[0];
            Long revenue = (Long) item[1];
            Product product = productRepository.findById(id).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, id)));
            if (product.getStatus() == false) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, id));
            }
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

        Long moneyPurchase = purchaseOrderRepository.findMoneyPurchaseByMonthAndYear(revenueRequest.year(), revenueRequest.month());
        return new RevenueResponse(
                revenueRequest.month(),
                revenueRequest.year(),
                revenueTotal,
                moneyPurchase,
                list
        );
    }
    public StatisticsResponse getDataHomePage(RevenueRequest revenueRequest) {
        Long employeeNumber = countEmployee();
        Long productNumber = productRepository.countProductsByStatusTrue();
        Long orderNumber = orderRepository.countOrdersByStatusAndMonth(revenueRequest.year(), revenueRequest.month());
        Long customerNumber = countCustomer();
        return new StatisticsResponse(
                employeeNumber,
                productNumber,
                orderNumber,
                customerNumber
        );
    }

    private Long countEmployee() {
        Long employeeNumber = peopleFeignClient.countEmployeeByStatusTrue().getBody();
        return employeeNumber;
    }

    private Long countCustomer() {
        Long customerNumber = peopleFeignClient.countCustomerByStatusTrue().getBody();
        return customerNumber;
    }

}
