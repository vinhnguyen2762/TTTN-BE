package product_service.service.impl;

import org.aspectj.weaver.ast.Or;
import org.springframework.stereotype.Service;
import product_service.dto.customer.CustomerAdminDto;
import product_service.dto.employee.EmployeeAdminDto;
import product_service.dto.order.OrderAddDto;
import product_service.dto.order.OrderAdminDto;
import product_service.dto.order.OrderUpdateDto;
import product_service.dto.orderDetail.OrderDetailAddDto;
import product_service.dto.orderDetail.OrderDetailAdminDto;
import product_service.dto.product.ProductAdminDto;
import product_service.enums.OrderStatus;
import product_service.exception.FailedException;
import product_service.exception.NotFoundException;
import product_service.model.Order;
import product_service.model.OrderDetail;
import product_service.model.Product;
import product_service.repository.OrderDetailRepository;
import product_service.repository.OrderRepository;
import product_service.repository.ProductRepository;
import product_service.service.OrderService;
import product_service.service.client.PeopleFeignClient;
import product_service.utils.Constants;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PeopleFeignClient peopleFeignClient;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, PeopleFeignClient peopleFeignClient, OrderDetailRepository orderDetailRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.peopleFeignClient = peopleFeignClient;
        this.orderDetailRepository = orderDetailRepository;
        this.productRepository = productRepository;
    }


    public List<OrderAdminDto> getAllOrderAdmin() {
        List<Order> orderList = orderRepository.findAll();
        return orderList.stream().map(o -> {
            CustomerAdminDto customerAdminDto = findCustomerById(o.getCustomerId());
            String customerName = customerAdminDto.firstName() + " " + customerAdminDto.lastName();

            EmployeeAdminDto employeeAdminDto = findEmployeeById(o.getUserId());
            String employeeName = employeeAdminDto.firstName() + " " + employeeAdminDto.lastName();

            List<OrderDetailAdminDto> list = o.getOrderDetails().stream().map(od -> {
                Product product = productRepository.findById(od.getProductId()).orElseThrow(
                        () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProductId())));
                if (product.getStatus() == false) {
                    throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProductId()));
                }
                String productName = product.getName();
                return OrderDetailAdminDto.fromOderDetail(od, productName);
            }).toList();
            return OrderAdminDto.fromOrder(o, customerName, employeeName, list);
        }).toList();
    }


    public OrderAdminDto addOrder(OrderAddDto orderAddDto) {
        LocalDate orderDate = LocalDate.now();
        Order orderAdd = new Order(
                orderDate,
                orderAddDto.customerId(),
                orderAddDto.userId()
        );
        orderRepository.saveAndFlush(orderAdd);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (OrderDetailAddDto orderDetailAddDto : orderAddDto.list()) {

            Double price = Double.parseDouble(orderDetailAddDto.price());
            Integer quantity = Integer.parseInt(orderDetailAddDto.quantity());

            OrderDetail orderDetail = new OrderDetail(
                    orderAdd,
                    orderDetailAddDto.productId(),
                    price,
                    quantity
            );
            orderDetailList.add(orderDetail);
        }

        orderDetailRepository.saveAll(orderDetailList);

        CustomerAdminDto customerAdminDto = findCustomerById(orderAdd.getCustomerId());
        String customerName = customerAdminDto.firstName() + " " + customerAdminDto.lastName();

        EmployeeAdminDto employeeAdminDto = findEmployeeById(orderAdd.getUserId());
        String employeeName = employeeAdminDto.firstName() + " " + employeeAdminDto.lastName();
        List<OrderDetailAdminDto> list = orderDetailList.stream().map(od -> {
            Product product = productRepository.findById(od.getProductId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProductId())));
            if (product.getStatus() == false) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProductId()));
            }
            String productName = product.getName();
            return OrderDetailAdminDto.fromOderDetail(od, productName);
        }).toList();

        return OrderAdminDto.fromOrder(orderAdd, customerName, employeeName ,list);
    }

    public OrderAdminDto updateOrder(Long id, OrderUpdateDto orderUpdateDto) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.ORDER_NOT_FOUND, id)));

        order.setCustomerId(orderUpdateDto.customerId());

        List<OrderDetail> updateList = order.getOrderDetails();
        updateList.clear();

        for (OrderDetailAddDto orderDetailAddDto : orderUpdateDto.list()) {

            Double price = Double.parseDouble(orderDetailAddDto.price());
            Integer quantity = Integer.parseInt(orderDetailAddDto.quantity());

            OrderDetail orderDetail = new OrderDetail(
                    order,
                    orderDetailAddDto.productId(),
                    price,
                    quantity
            );
            updateList.add(orderDetail);
        }

        orderRepository.save(order);

        CustomerAdminDto customerAdminDto = findCustomerById(order.getCustomerId());
        String customerName = customerAdminDto.firstName() + " " + customerAdminDto.lastName();
        EmployeeAdminDto employeeAdminDto = findEmployeeById(order.getUserId());
        String employeeName = employeeAdminDto.firstName() + " " + employeeAdminDto.lastName();
        List<OrderDetailAdminDto> list = updateList.stream().map(od -> {
            Product product = productRepository.findById(od.getProductId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProductId())));
            if (product.getStatus() == false) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProductId()));
            }
            String productName = product.getName();
            return OrderDetailAdminDto.fromOderDetail(od, productName);
        }).toList();

        return OrderAdminDto.fromOrder(order, customerName, employeeName,list);
    }

    public Long deleteOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.ORDER_NOT_FOUND, id)));
        if (order.getStatus().name().equals("DELETED")) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.ORDER_NOT_FOUND, id));
        }
        order.setStatus(OrderStatus.DELETED);
        orderRepository.saveAndFlush(order);
        return id;
    }

    public Long payOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.ORDER_NOT_FOUND, id)));
        if (order.getStatus().name().equals("DELETED")) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.ORDER_NOT_FOUND, id));
        } else if (order.getStatus().name().equals("PAID")) {
            throw new FailedException(String.format(Constants.ErrorMessage.ORDER_ALREADY_PAID, id));
        }
        for (OrderDetail o : order.getOrderDetails()) {
            Product product = productRepository.findById(o.getProductId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, o.getProductId())));
            if (product.getStatus() == false) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, o.getProductId()));
            }
            int quantity = product.getQuantity() - o.getQuantity();
            if (quantity < 0) {
                throw new FailedException(String.format(Constants.ErrorMessage.PRODUCT_NOT_ENOUGH, o.getProductId()));
            } else {
                product.setQuantity(quantity);
            }
            productRepository.saveAndFlush(product);
        }
        order.setStatus(OrderStatus.PAID);
        orderRepository.saveAndFlush(order);
        return id;
    }

    private EmployeeAdminDto findEmployeeById(Long id) {
        EmployeeAdminDto employeeAdminDto = peopleFeignClient.getEmployeeById(id).getBody();
        return employeeAdminDto;
    }

    private CustomerAdminDto findCustomerById(Long id) {
        CustomerAdminDto customerAdminDto = peopleFeignClient.getCustomerById(id).getBody();
        return customerAdminDto;
    }
}
