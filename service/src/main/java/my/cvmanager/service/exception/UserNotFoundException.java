package my.cvmanager.service.exception;

/**
 * Exception thrown when a user cannot be found in the system.
 * <p>
 * Typical use cases:
 * <ul>
 *   <li>Resetting a password for an email address that does not exist.</li>
 *   <li>Looking up a user by ID or username when no match is found.</li>
 * </ul>
 * <p>
 * This is an unchecked exception (extends {@link RuntimeException})
 * to avoid cluttering service signatures with mandatory throws clauses.
 */
public class UserNotFoundException extends RuntimeException {

    /**
     * Creates a new exception with no detail message.
     */
    public UserNotFoundException() {
        super();
    }

    /**
     * Creates a new exception with the specified detail message.
     *
     * @param message the detail message
     */
    public UserNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}
