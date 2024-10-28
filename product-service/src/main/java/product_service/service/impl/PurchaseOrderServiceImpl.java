package product_service.service.impl;

import org.springframework.stereotype.Service;
import product_service.dto.customer.CustomerAdminDto;
import product_service.dto.order.OrderAdminDto;
import product_service.dto.orderDetail.OrderDetailPostDto;
import product_service.dto.purchaseOrder.PurchaseOrderAdminDto;
import product_service.dto.purchaseOrder.PurchaseOrderAddDto;
import product_service.dto.purchaseOrder.PurchaseOrderUpdateDto;
import product_service.dto.purchaseOrderDetail.PurchaseOrderDetailAdminDto;
import product_service.dto.purchaseOrderDetail.PurchaseOrderDetailPostDto;
import product_service.dto.smallTrader.SmallTraderAdminDto;
import product_service.dto.supplier.SupplierAdminDto;
import product_service.enums.OrderStatus;
import product_service.exception.EmptyException;
import product_service.exception.FailedException;
import product_service.exception.NotFoundException;
import product_service.model.*;
import product_service.repository.ProductRepository;
import product_service.repository.PurchaseOrderDetailRepository;
import product_service.repository.PurchaseOrderRepository;
import product_service.service.PurchaseOrderService;
import product_service.service.client.PeopleFeignClient;
import product_service.utils.Constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.bouncycastle.asn1.x500.style.RFC4519Style.o;

@Service
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    private final PeopleFeignClient peopleFeignClient;
    private final ProductRepository productRepository;

    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository, PurchaseOrderDetailRepository purchaseOrderDetailRepository, PeopleFeignClient peopleFeignClient, ProductRepository productRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderDetailRepository = purchaseOrderDetailRepository;
        this.peopleFeignClient = peopleFeignClient;
        this.productRepository = productRepository;
    }

    public List<PurchaseOrderAdminDto> getAllPurchaseOrderAdmin() {
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll();
        return purchaseOrderList.stream().map(p -> {
            SmallTraderAdminDto smallTraderAdminDto = findSmallTraderById(p.getSmallTraderId());
            String smallTraderName = smallTraderAdminDto.firstName() + " " + smallTraderAdminDto.lastName();
            SupplierAdminDto supplierAdminDto = findSupplierById(p.getSupplierId());
            String supplierName = supplierAdminDto.firstName() + " " + supplierAdminDto.lastName();

            List<PurchaseOrderDetailAdminDto> list = p.getPurchaseOrderDetails().stream().map(pd -> {
                Product product = productRepository.findById(pd.getProductId()).orElseThrow(
                        () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, pd.getProductId())));
                if (product.getStatus() == false) {
                    throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, pd.getProductId()));
                }
                String productName = product.getName();
                return PurchaseOrderDetailAdminDto.fromPurchaseOrderDetail(pd, productName);
            }).toList();
            return PurchaseOrderAdminDto.fromPurchaseOrder(p, supplierAdminDto.taxId(),supplierName, list, smallTraderName);
        }).toList();
    }

    public Long addPurchaseOrder(PurchaseOrderAddDto purchaseOrderAddDto) {
        if (purchaseOrderAddDto.list().isEmpty()) {
            throw new EmptyException("List purchase order detail is null");
        }
        LocalDate deliveryDate = LocalDate.parse(purchaseOrderAddDto.deliveryDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate createDate = LocalDate.now();
        PurchaseOrder purchaseOrderAdd = new PurchaseOrder(
                purchaseOrderAddDto.supplierId(),
                deliveryDate,
                createDate,
                purchaseOrderAddDto.smallTraderId()
        );
        purchaseOrderRepository.saveAndFlush(purchaseOrderAdd);

        List<PurchaseOrderDetail> purchaseOrderDetailList = new ArrayList<>();
        for (PurchaseOrderDetailPostDto purchaseOrderDetailPostDto : purchaseOrderAddDto.list()) {

            Long supplyPrice = Long.parseLong(purchaseOrderDetailPostDto.supplyPrice());
            Integer quantity = Integer.parseInt(purchaseOrderDetailPostDto.quantity());

            PurchaseOrderDetail purchaseOrderDetail = new PurchaseOrderDetail(
                    supplyPrice,
                    purchaseOrderDetailPostDto.productId(),
                    quantity,
                    purchaseOrderAdd
            );
            purchaseOrderDetailList.add(purchaseOrderDetail);
        }

        purchaseOrderDetailRepository.saveAll(purchaseOrderDetailList);

        SupplierAdminDto supplierAdminDto = findSupplierById(purchaseOrderAdd.getSupplierId());
        String supplierName = supplierAdminDto.firstName() + " " + supplierAdminDto.lastName();

        List<PurchaseOrderDetailAdminDto> list = purchaseOrderDetailList.stream().map(pd -> {
            Product product = productRepository.findById(pd.getProductId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, pd.getProductId())));
            if (product.getStatus() == false) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, pd.getProductId()));
            }
            String productName = product.getName();
            return PurchaseOrderDetailAdminDto.fromPurchaseOrderDetail(pd, productName);
        }).toList();
        return purchaseOrderAdd.getId();
    }

    public Long updatePurchaseOrder(Long id, PurchaseOrderUpdateDto purchaseOrderUpdateDto) {
        if (purchaseOrderUpdateDto.list().isEmpty()) {
            throw new EmptyException("List purchase order detail is null");
        }
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.PURCHASE_ORDER_NOT_FOUND, id)));
        if (purchaseOrder.getStatus().name().equals("DELETED")) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PURCHASE_ORDER_NOT_FOUND, id));
        }

        LocalDate deliveryDate = LocalDate.parse(purchaseOrderUpdateDto.deliveryDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        purchaseOrder.setDeliveryDate(deliveryDate);

        List<PurchaseOrderDetail> updateList = purchaseOrder.getPurchaseOrderDetails();
        updateList.clear();

        for (PurchaseOrderDetailPostDto purchaseOrderDetailPostDto : purchaseOrderUpdateDto.list()) {

            Long supplyPrice = Long.parseLong(purchaseOrderDetailPostDto.supplyPrice());
            Integer quantity = Integer.parseInt(purchaseOrderDetailPostDto.quantity());

            PurchaseOrderDetail purchaseOrderDetail = new PurchaseOrderDetail(
                    supplyPrice,
                    purchaseOrderDetailPostDto.productId(),
                    quantity,
                    purchaseOrder
            );
            updateList.add(purchaseOrderDetail);
        }

        purchaseOrderRepository.save(purchaseOrder);

        SupplierAdminDto supplierAdminDto = findSupplierById(purchaseOrder.getSupplierId());
        String supplierName = supplierAdminDto.firstName() + " " + supplierAdminDto.lastName();

        List<PurchaseOrderDetailAdminDto> list = updateList.stream().map(pd -> {
            Product product = productRepository.findById(pd.getProductId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, pd.getProductId())));
            if (product.getStatus() == false) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, pd.getProductId()));
            }
            String productName = product.getName();
            return PurchaseOrderDetailAdminDto.fromPurchaseOrderDetail(pd, productName);
        }).toList();
        return purchaseOrder.getId();
    }

    public Long deletePurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.PURCHASE_ORDER_NOT_FOUND, id)));
        if (purchaseOrder.getStatus().name().equals("DELETED")) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PURCHASE_ORDER_NOT_FOUND, id));
        }
        purchaseOrder.setStatus(OrderStatus.DELETED);
        purchaseOrderRepository.saveAndFlush(purchaseOrder);
        return id;
    }

    public Long payPurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(id).orElseThrow(
                () -> new NotFoundException(String.format(Constants.ErrorMessage.PURCHASE_ORDER_NOT_FOUND, id)));
        if (purchaseOrder.getStatus().name().equals("DELETED")) {
            throw new NotFoundException(String.format(Constants.ErrorMessage.PURCHASE_ORDER_NOT_FOUND, id));
        } else if (purchaseOrder.getStatus().name().equals("PAID")) {
            throw new FailedException(String.format(Constants.ErrorMessage.PURCHASE_ORDER_NOT_FOUND, id));
        }
        for (PurchaseOrderDetail po : purchaseOrder.getPurchaseOrderDetails()) {
            Product product = productRepository.findById(po.getProductId()).orElseThrow(
                    () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, po.getProductId())));
            if (product.getStatus() == false) {
                throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, po.getProductId()));
            }
            int quantity = product.getQuantity() + po.getQuantity();
            product.setQuantity(quantity);
            productRepository.saveAndFlush(product);
        }
        purchaseOrder.setStatus(OrderStatus.PAID);
        purchaseOrderRepository.saveAndFlush(purchaseOrder);
        return id;
    }

    public List<PurchaseOrderAdminDto> getAllPurchaseOrderSmallTraderId(Long id) {
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findBySmallTraderId(id);
        return purchaseOrderList.stream().map(p -> {
            SmallTraderAdminDto smallTraderAdminDto = findSmallTraderById(p.getSmallTraderId());
            String smallTraderName = smallTraderAdminDto.firstName() + " " + smallTraderAdminDto.lastName();
            SupplierAdminDto supplierAdminDto = findSupplierById(p.getSupplierId());
            String supplierName = supplierAdminDto.firstName() + " " + supplierAdminDto.lastName();

            List<PurchaseOrderDetailAdminDto> list = p.getPurchaseOrderDetails().stream().map(pd -> {
                Product product = productRepository.findById(pd.getProductId()).orElseThrow(
                        () -> new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, pd.getProductId())));
                if (product.getStatus() == false) {
                    throw new NotFoundException(String.format(Constants.ErrorMessage.PRODUCT_NOT_FOUND, pd.getProductId()));
                }
                String productName = product.getName();
                return PurchaseOrderDetailAdminDto.fromPurchaseOrderDetail(pd, productName);
            }).toList();
            return PurchaseOrderAdminDto.fromPurchaseOrder(p, supplierAdminDto.taxId(),supplierName, list, smallTraderName);
        }).toList();
    }

    private SupplierAdminDto findSupplierById(Long id) {
        SupplierAdminDto supplierAdminDto = peopleFeignClient.getSupplierById(id).getBody();
        return supplierAdminDto;
    }

    private SmallTraderAdminDto findSmallTraderById(Long id) {
        SmallTraderAdminDto smallTraderAdminDto = peopleFeignClient.getById(id).getBody();
        return smallTraderAdminDto;
    }
}
