package product_service.service.impl;

import org.springframework.stereotype.Service;
import product_service.dto.customer.CustomerAdminDto;
import product_service.dto.smallTrader.SmallTraderAdminDto;
import product_service.dto.order.OrderAddDto;
import product_service.dto.order.OrderAdminDto;
import product_service.dto.order.OrderUpdateDto;
import product_service.dto.orderDetail.OrderDetailPostDto;
import product_service.dto.orderDetail.OrderDetailAdminDto;
import product_service.enums.OrderStatus;
import product_service.exception.EmptyException;
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

            SmallTraderAdminDto smallTraderAdminDto = findSmallTraderById(o.getSmallTraderId());
            String smallTraderName = smallTraderAdminDto.firstName() + " " + smallTraderAdminDto.lastName();

            String phoneNumber = customerAdminDto.phoneNumber();

            List<OrderDetailAdminDto> list = o.getOrderDetails().stream().map(od -> {
                Product product = productRepository.findById(od.getProductId()).orElseThrow(
                        () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProductId())));
                if (product.getStatus() == false) {
                    throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProductId()));
                }
                String productName = product.getName();
                return OrderDetailAdminDto.fromOderDetail(od, productName);
            }).toList();
            return OrderAdminDto.fromOrder(o, customerName, smallTraderName, phoneNumber, list);
        }).toList();
    }


    public OrderAdminDto addOrder(OrderAddDto orderAddDto) {
        if (orderAddDto.list().isEmpty()) {
            throw new EmptyException("List order detail is null");
        }
        LocalDate orderDate = LocalDate.now();
        Order orderAdd = new Order(
                orderDate,
                orderAddDto.customerId(),
                orderAddDto.smallTraderId()
        );
        orderRepository.saveAndFlush(orderAdd);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (OrderDetailPostDto orderDetailPostDto : orderAddDto.list()) {

            Long price = Long.parseLong(orderDetailPostDto.price());
            Integer quantity = Integer.parseInt(orderDetailPostDto.quantity());

            OrderDetail orderDetail = new OrderDetail(
                    orderAdd,
                    orderDetailPostDto.productId(),
                    price,
                    quantity
            );
            orderDetailList.add(orderDetail);
        }

        orderDetailRepository.saveAll(orderDetailList);

        CustomerAdminDto customerAdminDto = findCustomerById(orderAdd.getCustomerId());
        String customerName = customerAdminDto.firstName() + " " + customerAdminDto.lastName();

        SmallTraderAdminDto smallTraderAdminDto = findSmallTraderById(orderAdd.getSmallTraderId());
        String employeeName = smallTraderAdminDto.firstName() + " " + smallTraderAdminDto.lastName();

        String phoneNumber = customerAdminDto.phoneNumber();

        List<OrderDetailAdminDto> list = orderDetailList.stream().map(od -> {
            Product product = productRepository.findById(od.getProductId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProductId())));
            if (product.getStatus() == false) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProductId()));
            }
            String productName = product.getName();
            return OrderDetailAdminDto.fromOderDetail(od, productName);
        }).toList();

        return OrderAdminDto.fromOrder(orderAdd, customerName, employeeName, phoneNumber, list);
    }

    public OrderAdminDto updateOrder(Long id, OrderUpdateDto orderUpdateDto) {
        if (orderUpdateDto.list().isEmpty()) {
            throw new EmptyException("List order detail is null");
        }
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.ORDER_NOT_FOUND, id)));
        if (order.getStatus().name().equals("DELETED")) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.ORDER_NOT_FOUND, id));
        }

        List<OrderDetail> updateList = order.getOrderDetails();
        updateList.clear();

        for (OrderDetailPostDto orderDetailPostDto : orderUpdateDto.list()) {

            Long price = Long.parseLong(orderDetailPostDto.price());;
            Integer quantity = Integer.parseInt(orderDetailPostDto.quantity());

            OrderDetail orderDetail = new OrderDetail(
                    order,
                    orderDetailPostDto.productId(),
                    price,
                    quantity
            );
            updateList.add(orderDetail);
        }

        orderRepository.save(order);

        CustomerAdminDto customerAdminDto = findCustomerById(order.getCustomerId());
        String customerName = customerAdminDto.firstName() + " " + customerAdminDto.lastName();

        SmallTraderAdminDto smallTraderAdminDto = findSmallTraderById(order.getSmallTraderId());
        String employeeName = smallTraderAdminDto.firstName() + " " + smallTraderAdminDto.lastName();

        String phoneNumber = customerAdminDto.phoneNumber();

        List<OrderDetailAdminDto> list = updateList.stream().map(od -> {
            Product product = productRepository.findById(od.getProductId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProductId())));
            if (product.getStatus() == false) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProductId()));
            }
            String productName = product.getName();
            return OrderDetailAdminDto.fromOderDetail(od, productName);
        }).toList();

        return OrderAdminDto.fromOrder(order, customerName, employeeName, phoneNumber, list);
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
                throw new EmptyException(String.format(Constants.ErrorMessage.PRODUCT_NOT_ENOUGH, o.getProductId()));
            } else {
                product.setQuantity(quantity);
            }
            productRepository.saveAndFlush(product);
        }
        order.setStatus(OrderStatus.PAID);
        orderRepository.saveAndFlush(order);
        return id;
    }

    public Boolean checkCustomerHasOrder(Long id) {
        List<Order> rs = orderRepository.findByCustomerId(id);
        return rs.isEmpty() ? false : true;
    }

    public List<OrderAdminDto> getAllOrderSmallTraderId(Long id) {
        List<Order> orderList = orderRepository.findBySmallTraderId(id);
        return orderList.stream().map(o -> {
            CustomerAdminDto customerAdminDto = findCustomerById(o.getCustomerId());
            String customerName = customerAdminDto.firstName() + " " + customerAdminDto.lastName();

            SmallTraderAdminDto smallTraderAdminDto = findSmallTraderById(o.getSmallTraderId());
            String smallTraderName = smallTraderAdminDto.firstName() + " " + smallTraderAdminDto.lastName();

            String phoneNumber = customerAdminDto.phoneNumber();

            List<OrderDetailAdminDto> list = o.getOrderDetails().stream().map(od -> {
                Product product = productRepository.findById(od.getProductId()).orElseThrow(
                        () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProductId())));
                if (product.getStatus() == false) {
                    throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProductId()));
                }
                String productName = product.getName();
                return OrderDetailAdminDto.fromOderDetail(od, productName);
            }).toList();
            return OrderAdminDto.fromOrder(o, customerName, smallTraderName, phoneNumber, list);
        }).toList();
    }

    private SmallTraderAdminDto findSmallTraderById(Long id) {
        SmallTraderAdminDto smallTraderAdminDto = peopleFeignClient.getById(id).getBody();
        return smallTraderAdminDto;
    }

    private CustomerAdminDto findCustomerById(Long id) {
        CustomerAdminDto customerAdminDto = peopleFeignClient.getCustomerById(id).getBody();
        return customerAdminDto;
    }
}
