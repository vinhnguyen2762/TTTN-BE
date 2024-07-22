package people_service.exception;

public class FailedException extends RuntimeException {
    public FailedException(String message) {
        super(message);
    }
}
