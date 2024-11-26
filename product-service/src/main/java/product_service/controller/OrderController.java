package product_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import product_service.dto.order.OrderAddDto;
import product_service.dto.order.OrderAdminDto;
import product_service.dto.order.OrderUpdateDto;
import product_service.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<OrderAdminDto>> getAllOrderAdmin() {
        List<OrderAdminDto> list = orderService.getAllOrderAdmin();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/get-all/{id}")
    public ResponseEntity<List<OrderAdminDto>> getAllOrderSmallTrader(@PathVariable Long id) {
        List<OrderAdminDto> list = orderService.getAllOrderSmallTraderId(id);
        return ResponseEntity.ok().body(list);
    }

    @PostMapping("/add")
    public ResponseEntity<OrderAdminDto> addOder(@RequestBody OrderAddDto orderAddDto) {
        OrderAdminDto order = orderService.addOrder(orderAddDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<OrderAdminDto> updateOrder(@PathVariable Long id, @RequestBody OrderUpdateDto orderUpdateDto) {
        OrderAdminDto orderAdminDto = orderService.updateOrder(id, orderUpdateDto);
        return ResponseEntity.ok().body(orderAdminDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Long> deleteOrder(@PathVariable Long id) {
        Long deleteId = orderService.deleteOrder(id);
        return ResponseEntity.ok().body(deleteId);
    }

    @PostMapping("/pay/{id}")
    public ResponseEntity<Long> payOrder(@PathVariable Long id) {
        Long payId = orderService.payOrder(id);
        return ResponseEntity.ok().body(payId);
    }

    @GetMapping("/customer/{id}")
    public ResponseEntity<Boolean> checkCustomerHasOrder(@PathVariable Long id) {
        Boolean rs = orderService.checkCustomerHasOrder(id);
        return ResponseEntity.ok().body(rs);
    }
}
