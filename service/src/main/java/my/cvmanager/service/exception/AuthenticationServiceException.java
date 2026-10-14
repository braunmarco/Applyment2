package my.cvmanager.service.exception;

public class AuthenticationServiceException extends RuntimeException {
    public AuthenticationServiceException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}
