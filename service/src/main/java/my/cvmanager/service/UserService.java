package my.cvmanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import my.cvmanager.domain.User;
import my.cvmanager.repositories.UserDao;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
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
    private final UserDao userDataProvider = new UserDao();

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

        try {
            userDataProvider.persist(user, em); // persist the user
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
        userDataProvider.find(userId, em).ifPresentOrElse(user -> {
            if (isAdmin(user)) {
                logger.info("User " + user.getUsername() + " cannot be unregistered.");
            } else {
                userDataProvider.delete(user, em);
                logger.info("User has been unregistered successfully");
            }
        }, () -> logger.warning("User not found"));
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
            Optional<User> user = userDataProvider.findOne("username", username, em);
            if (user.isPresent() && user.get().getPassword().equals(password)) {
                user.get().setLoggedIn(true); // mark user as logged-in.
                return userDataProvider.update(user.get(), em); // update user
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
        Optional<User> user = userDataProvider.find(userId, em);
        if (user.isPresent()) {
            user.get().setLoggedIn(false);
            userDataProvider.update(user.get(), em); // update user
        }
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
        Optional<User> user = userDataProvider.findOne("username", username, em);
        return user.isPresent() && user.get().getPassword().equals(password);
    }

    /**
     * Sends the credentials of a user to the user.
     *
     * @param email the email address of the user
     */
    @Transactional
    @Override
    public void sendCredentials(String email) {
        Optional<User> user = userDataProvider.findOne("email", email, em);
        if (user.isPresent()) {
            String credentials = "username: " + user.get().getUsername() + " password: " + user.get().getPassword();
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
            Optional<User> user = userDataProvider.findOne(params, em);
            return user.orElse(null);
        } catch (Exception ex) {
            logger.severe("Error checking if user is registered: " + ex.getMessage());
        }

        return null;
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
