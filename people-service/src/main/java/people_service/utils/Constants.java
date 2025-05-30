package people_service.utils;

public class Constants {

    public final class ErrorMessage {
        public static final String USER_NOT_FOUND = "User with email: %s not found";
        public static final String USER_NOT_FOUND_ID = "User with id: %s not found";
        public static final String TOKEN_NOT_FOUND = "Token with value: %s not found";
        public static final String EMAIL_ALREADY_TAKEN = "Email with value: %s already taken";
        public static final String FAILED_TO_SEND_EMAIL = "Failed to send email to: %s";
        public static final String EMAIL_NOT_VALID = "Email with value: %s is not valid";
        public static final String PHONE_NUMBER_ALREADY_TAKEN = "Phone number: %s already taken";
        public static final String CUSTOMER_NOT_FOUND = "Customer with id: %s not found";
        public static final String CUSTOMER_NOT_FOUND_PHONE_NUMBER = "Customer with phone number: %s not found";
        public static final String SUPPLIER_ALREADY_TAKEN = "Supplier with tax id: %s already taken";
        public static final String SUPPLIER_NOT_FOUND = "Supplier with id: %s not found";
        public static final String SUPPLIER_NOT_FOUND_TAX_ID = "Supplier with tax id: %s not found";
        public static final String SMALL_TRADER_NOT_FOUND = "SmallTrader with id: %s not found";
        public static final String USER_NOT_EXIST = "Small trader with email: %s not exist";
        public static final String ACCOUNT_IS_LOCKED = "Account with username: %s is locked";
        public static final String PASSWORD_NOT_CORRECT = "Password is not correct";
        public static final String DEBT_DETAIL_NOT_FOUND = "Debt detail with id: %s not found";
        public static final String PRODUCER_NOT_FOUND = "Producer with id: %s not found";
        public static final String CUSTOMER_CANT_DELETE = "Customer with id: %s can't be deleted because has debt not pay yet";
        public static final String PAID_MORE_THAN_DEBT = "Paid amount is more than debt amount";
        public static final String VERIFY_CODE_FALSE = "Verify code send to email: %s is not true";
        public static final String EMPLOYEE_NOT_FOUND = "Employee with id: %s not found";

    }

}
