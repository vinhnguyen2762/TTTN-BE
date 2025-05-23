package product_service.utils;

public class Constants {

    public final class ErrorMessage {
        public static final String PRODUCT_NOT_FOUND = "Product with id: %s not found";
        public static final String PRODUCT_NOT_FOUND_NAME = "Product with name: %s not found";
        public static final String PRODUCT_ALREADY_TAKEN = "Product with name: %s already taken";
        public static final String IMAGE_UPLOAD_FAIL = "Image with name: %s upload fail";
        public static final String ORDER_NOT_FOUND = "Order with id: %s not found";
        public static final String PURCHASE_ORDER_NOT_FOUND = "Purchase order with id: %s not found";
        public static final String PRODUCT_NOT_ENOUGH = "Product with id: %s not enough";
        public static final String ORDER_ALREADY_PAID = "Order with id: %s already paid";
        public static final String PURCHASE_ORDER_ALREADY_PAID = "Purchase order with id: %s already paid";
        public static final String PROMOTION_NOT_FOUND = "Promotion with id: %s not found";
        public static final String PROMOTION_ALREADY_TAKEN = "Promotion with name: %s already taken";
        public static final String PRODUCT_ALREADY_HAS_PROMOTION = "Some product already has promotion";
        public static final String PROMOTION_VALUE_NOT_VALID = "Promotion value is more than product price";

    }

}
