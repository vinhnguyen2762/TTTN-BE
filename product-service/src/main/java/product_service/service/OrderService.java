package product_service.service;

import product_service.dto.order.OrderAddDto;
import product_service.dto.order.OrderAdminDto;
import product_service.dto.order.OrderUpdateDto;
import product_service.dto.purchaseOrder.PurchaseOrderAdminDto;
import product_service.dto.revenue.RevenueRequest;
import product_service.dto.revenue.RevenueResponse;

import java.util.List;

public interface OrderService {
    public List<OrderAdminDto> getAllOrderAdmin();
    public Long addOrder(OrderAddDto orderAddDto);
    public Long updateOrder(Long id, OrderUpdateDto orderUpdateDto);
    public Long deleteOrder(Long id);
    public Long payOrder(Long id);
    public Boolean checkCustomerHasOrder(Long id);
    public List<OrderAdminDto> getAllOrderSmallTraderId(Long id);
}
