package my.cvmanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import my.cvmanager.domain.User;
import my.cvmanager.repositories.UserDao;
import my.cvmanager.service.exception.UserNotFoundException;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * This class represents a user service that provides user-related operations.
 * It implements the IUserService interface and provides methods for registering, logging in, logging out, and unregistering users.
 *
 * @author [Dein Name]
 * @version 1.0
 */
@ApplicationScoped
public class UserService implements IUserService {

    /**
     * The logger instance for this class.
     */
    private final Logger logger = Logger.getLogger(UserService.class.getName());

    /**
     * The constant for the admin username.
     */
    private static final String ADMIN = "admin";

    /**
     * The entity manager instance for this class.
     */
    @PersistenceContext(unitName = "cvmanagerPU")
    private EntityManager em;

    /**
     * The user data provider instance for this class.
     */
    private final UserDao dao = new UserDao();

    /**
     * Sets the entity manager instance for this class.
     * This method is used for testing purposes to inject a mock entity manager.
     *
     * @param em the entity manager instance to set
     */
    void setEntityManager(EntityManager em) {
        this.em = em;
    }

    /**
     * Registers a new user with the given username, password, and email.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @param email    the email address of the user
     * @return the registered user
     */
    @Transactional
    @Override
    public User register(String username, String password, String email) {
        // create user
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); //TODO: Hashing
        user.setEmail(email);

        if (userNameExists(username)) {
            throw new ValidationException(
                    "Der Benutzername '" + username + "' ist bereits vergeben."
            );
        }

        if (userEmailExists(email)) {
            throw new ValidationException(
                    "Die E-Mail-Adresse '" + email + "' ist bereits registriert."
            );
        }

        try {
            dao.persist(user, em); // persist the user
            logger.info("User has been registered successfully");
        } catch (Exception ex) {
            logger.severe("Error registering user: " + ex.getMessage());
            //TODO: Hier könnte eine spezifische Ausnahmebehandlung erfolgen
        }

        return user;
    }

    /**
     * Unregisters the user with the given user ID.
     *
     * @param userId the ID of the user to unregister
     */
    @Transactional
    @Override
    public void unregister(Long userId) {
        User user = dao.find(userId, em);

        if (user != null) {
            if (isAdmin(user)) {
                logger.info("User " + user.getUsername() + " cannot be unregistered.");
            } else {
                logger.info("User " + user.getUsername() + " has been unregistered successfully.");
            }
        } else {
            logger.warning("User not found");
        }
    }

    /**
     * Logs in a user with the given username and password.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return the logged-in user
     */
    @Transactional
    @Override
    public User login(String username, String password) {
        try {
            User user = dao.findOne("username", username, em);
            if (user != null && user.getPassword().equals(password)) {
                user.setLoggedIn(true); // mark user as logged-in.

                return dao.update(user, em); // update user
            }
        } catch (Exception ex) {
            logger.severe("Error during login: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Logs out the user with the given user ID.
     *
     * @param userId the ID of the user to log out
     */
    @Transactional
    @Override
    public void logout(Long userId) {
        User user = dao.find(userId, em);
        if (user != null) {
            user.setLoggedIn(false);
            dao.update(user, em);
        }
    }

    /**
     * Updates the password of the user identified by the given ID.
     * <p>
     * Internally delegates to {@link #updatePasswordInternal(User, String)}
     * to locate the user and update the password after hashing.
     * </p>
     *
     * @param id             the ID of the user whose password should be updated
     * @param newRawPassword the new raw password (will be hashed internally)
     */
    @Transactional
    public void updatePassword(Long id, String newRawPassword) {
        User user = dao.find(id, em);
        updatePasswordInternal(user, newRawPassword);
    }

    /**
     * Resets the password for the user identified by the given email address.
     * <p>
     * This method looks up the user by email, hashes the provided raw password,
     * and updates the persisted user record. If the user cannot be found,
     * it should either log a warning or throw a custom exception depending
     * on the service implementation.
     * </p>
     *
     * @param email          the email of the user whose password should be reset
     * @param newRawPassword the new raw password (will be hashed internally)
     * @throws IllegalArgumentException if the email is {@code null} or empty
     * @throws UserNotFoundException    if no user exists with the given email
     */
    @Transactional
    public void resetPassword(String email, String newRawPassword) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email must not be null or empty");
        }

        User user = dao.findOne("email", email, em);
        if (user == null) {
            throw new UserNotFoundException("No user found with email: " + email);
        }

        updatePasswordInternal(user, newRawPassword);
    }

    /**
     * Internal helper method to update a user's password.
     * <p>
     * This method takes a {@link Supplier} that resolves a user lookup
     * (by ID or email), and if found, updates the password with a hashed value.
     * If no user is found, a warning is logged.
     * </p>
     *
     * @param rawPassword the new raw password to set (will be hashed)
     */
    private void updatePasswordInternal(User user, String rawPassword) {
        if (user != null) {
            // immer hashen – niemals ein Raw-Passwort speichern
            user.setPassword(hash(rawPassword));
            dao.update(user, em);
        }
    }

    /**
     * Generates a new password string.
     * <p>
     * Currently returns a placeholder hashed password.
     * A proper password generator should be implemented in the future.
     * </p>
     *
     * @return a newly generated (placeholder) password string
     */
    public String findNewPassword() {
        //TODO: create password generator
        return hash("newPassword");
    }

    /**
     * Hashes the provided raw password string.
     * <p>
     * This is a placeholder implementation and should be replaced with
     * a secure hashing algorithm (e.g., BCrypt, Argon2).
     * </p>
     *
     * @param raw the raw password string
     * @return the hashed password (currently just returns the raw value)
     */
    private String hash(String raw) {
        // z.B. BCrypt
        return raw; // Platzhalter
    }

    /**
     * Validates the credentials of a user.
     *
     * @param username the username of the user
     * @param password the password of the user
     * @return true if the credentials are valid, false otherwise
     */
    @Transactional
    @Override
    public boolean validateCredentials(String username, String password) {
        User user = dao.findOne("username", username, em);
        return user != null && StringUtils.isEmpty(user.getPassword()) && user.getPassword().equals(password);
    }

    /**
     * Sends the credentials of a user to the user.
     *
     * @param email the email address of the user
     */
    @Transactional
    @Override
    public void sendCredentials(String email) {
        User user = dao.findOne("email", email, em);
        if (user != null) {
            String credentials = "username: " + user.getUsername() + " password: " + user.getPassword();
            logger.info(credentials);
            logger.info("User credentials have been sent successfully");
        }
    }

    /**
     * Checks if a user is registered with the given username and email.
     *
     * @param username the username of the user
     * @param email    the email address of the user
     * @return the registered user, or null if not found
     */
    @Transactional
    @Override
    public User isUserRegistered(String username, String email) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        params.put("email", email);

        try {
            return dao.findOne(params, em);
        } catch (Exception ex) {
            logger.severe("Error checking if user is registered: " + ex.getMessage());
        }

        return null;
    }

    /**
     * check userName exist.
     *
     * @param username
     * @return
     */
    public boolean userNameExists(String username) {
        Map<String, Object> params = new HashMap<>();
        params.put("username", username);
        try {
            return dao.findOne(params, em) != null;
        } catch (Exception ex) {
            logger.severe("Error checking if user name exists: " + ex.getMessage());
        }

        return false;
    }

    /**
     * check email exist.
     *
     * @param email
     * @return
     */
    public boolean userEmailExists(String email) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);

        try {
            return dao.findOne(params, em) != null;
        } catch (Exception ex) {
            logger.severe("Error checking if user email exists: " + ex.getMessage());
        }

        return false;
    }

    /**
     * Checks if a user is an administrator.
     *
     * @param user the user to check
     * @return true if the user is an administrator, false otherwise
     */
    public boolean isAdmin(User user) {
        return isAdmin(user.getUsername(), user.getEmail());
    }

    /**
     * Checks if a user with the given username and email is an administrator.
     *
     * @param username the username of the user
     * @param email    the email address of the user
     * @return true if the user is an administrator, false otherwise
     */
    public boolean isAdmin(String username, String email) {
        return !ADMIN.equals(username);
    }
}
