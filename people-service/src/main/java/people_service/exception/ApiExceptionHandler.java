package people_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice // tell Spring that this is a class which handlers exceptions
public class ApiExceptionHandler {

    @ExceptionHandler(value = NotFoundException.class)
    public ResponseEntity<ExceptionDto> handlerNotFoundException(NotFoundException e) {
        HttpStatus notFound = HttpStatus.NOT_FOUND;
        ExceptionDto exceptionDto = new ExceptionDto(
                e.getMessage(), notFound, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(exceptionDto, notFound);
    }

    @ExceptionHandler(value = DuplicateException.class)
    public ResponseEntity<ExceptionDto> handlerDuplicateException(DuplicateException e) {
        HttpStatus conflict = HttpStatus.CONFLICT;
        ExceptionDto exceptionDto = new ExceptionDto(
                e.getMessage(), conflict, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(exceptionDto, conflict);
    }

    @ExceptionHandler(value = FailedException.class)
    public ResponseEntity<ExceptionDto> handlerFailedException(FailedException e) {
        HttpStatus badRequest = HttpStatus.BAD_REQUEST;
        ExceptionDto exceptionDto = new ExceptionDto(
                e.getMessage(), badRequest, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(exceptionDto, badRequest);
    }

    @ExceptionHandler(value = AccountLockedException.class)
    public ResponseEntity<ExceptionDto> handlerAccountLockedException(AccountLockedException e) {
        HttpStatus locked = HttpStatus.LOCKED;
        ExceptionDto exceptionDto = new ExceptionDto(
                e.getMessage(), locked, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(exceptionDto, locked);
    }
}
