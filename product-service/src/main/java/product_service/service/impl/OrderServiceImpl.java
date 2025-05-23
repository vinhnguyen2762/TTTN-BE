package product_service.service.impl;

import org.springframework.stereotype.Service;
import product_service.dto.customer.CustomerAdminDto;
import product_service.dto.order.OrderDebt;
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
            String smallTraderPhoneNumber = smallTraderAdminDto.phoneNumber();

            String phoneNumber = customerAdminDto.phoneNumber();

            List<OrderDetailAdminDto> list = o.getOrderDetails().stream().map(od -> {
                Product product = productRepository.findById(od.getProduct().getId()).orElseThrow(
                        () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProduct().getId())));
                String productName = product.getName();
                return OrderDetailAdminDto.fromOderDetail(od, productName);
            }).toList();
            return OrderAdminDto.fromOrder(o, customerName, smallTraderName, phoneNumber, list, smallTraderPhoneNumber);
        }).toList();
    }


    public Long addOrder(OrderAddDto orderAddDto) {
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

            Product product = productRepository.findById(orderDetailPostDto.productId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, orderDetailPostDto.productId())));

            OrderDetail orderDetail = new OrderDetail(
                    orderAdd,
                    product,
                    price,
                    quantity
            );
            orderDetailList.add(orderDetail);
        }

        orderDetailRepository.saveAll(orderDetailList);

        return orderAdd.getId();
    }

    public Long updateOrder(Long id, OrderUpdateDto orderUpdateDto) {
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

            Product product = productRepository.findById(orderDetailPostDto.productId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, orderDetailPostDto.productId())));

            OrderDetail orderDetail = new OrderDetail(
                    order,
                    product,
                    price,
                    quantity
            );
            updateList.add(orderDetail);
        }

        orderRepository.save(order);

        return order.getId();
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
            Product product = productRepository.findById(o.getProduct().getId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, o.getProduct().getId())));
            if (product.getStatus() == false) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, o.getProduct().getId()));
            }
            int quantity = product.getQuantity() - o.getQuantity();
            if (quantity < 0) {
                throw new EmptyException(String.format(Constants.ErrorMessage.PRODUCT_NOT_ENOUGH, o.getProduct().getId()));
            } else {
                product.setQuantity(quantity);
            }
            productRepository.saveAndFlush(product);
        }

        LocalDate paidDate = LocalDate.now();

        order.setPaidDate(paidDate);
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
            String smallTraderPhoneNumber = smallTraderAdminDto.phoneNumber();

            String phoneNumber = customerAdminDto.phoneNumber();

            List<OrderDetailAdminDto> list = o.getOrderDetails().stream().map(od -> {
                Product product = productRepository.findById(od.getProduct().getId()).orElseThrow(
                        () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProduct().getId())));
                String productName = product.getName();
                return OrderDetailAdminDto.fromOderDetail(od, productName);
            }).toList();
            return OrderAdminDto.fromOrder(o, customerName, smallTraderName, phoneNumber, list, smallTraderPhoneNumber);
        }).toList();
    }

    public List<OrderDebt> getCustomerDebtOrder(Long id) {
        List<Order> list = orderRepository.customerDebtOrder(id);
        return list.stream().map(OrderDebt::fromOrder).toList();
    }

    public OrderAdminDto getById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.ORDER_NOT_FOUND, id)));
        if (order.getStatus().name().equals("DELETED")) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.ORDER_NOT_FOUND, id));
        }

        CustomerAdminDto customerAdminDto = findCustomerById(order.getCustomerId());
        String customerName = customerAdminDto.firstName() + " " + customerAdminDto.lastName();

        SmallTraderAdminDto smallTraderAdminDto = findSmallTraderById(order.getSmallTraderId());
        String smallTraderName = smallTraderAdminDto.firstName() + " " + smallTraderAdminDto.lastName();
        String smallTraderPhoneNumber = smallTraderAdminDto.phoneNumber();

        String phoneNumber = customerAdminDto.phoneNumber();

        List<OrderDetailAdminDto> list = order.getOrderDetails().stream().map(od -> {
            Product product = productRepository.findById(od.getProduct().getId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, od.getProduct().getId())));
            String productName = product.getName();
            return OrderDetailAdminDto.fromOderDetail(od, productName);
        }).toList();
        return OrderAdminDto.fromOrder(order, customerName, smallTraderName, phoneNumber, list, smallTraderPhoneNumber);

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
