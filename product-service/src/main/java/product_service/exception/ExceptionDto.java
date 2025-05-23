package product_service.exception;

import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public record ExceptionDto(String message, HttpStatus httpStatus, ZonedDateTime timestamp) {

}
