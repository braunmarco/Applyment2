package my.cvmanager.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import my.cvmanager.domain.Role;
import my.cvmanager.domain.User;
import my.cvmanager.domain.UserRole;
import my.cvmanager.repositories.UserRoleDao;
import my.cvmanager.service.dto.RegisterUserRequest;
import my.cvmanager.service.exception.AuthenticationException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class UserServiceTest {
    //private Logger logger = Logger.getLogger(UserServiceTest.class.getName());
    private static final String TEST_USERNAME = "testUser";
    private static final String TEST_PWD_1234 = "testPwd1234!";
    private static final String TEST_USER_MAIL = "testUser@example.com";
    private static final LocalDateTime TEST_NOW = LocalDateTime.of(2026, 10, 14, 12, 0);
    private EntityManagerFactory emf;
    private EntityManager em;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("cvmanagerTestPU");
        em = emf.createEntityManager();
        userService = new UserService();
        userService.setEntityManager(em);
    }

    @AfterEach
    public void tearDown() {
        if (em != null) em.close();
        if (emf != null) emf.close();
    }

    @Test
    public void testRegisterAssignUserRole() {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest("testUser", TEST_PWD_1234, TEST_USER_MAIL);
        User user = inTransaction(() -> userService.register(registerUserRequest));
        Assertions.assertNotNull(user.getId());

        em.clear();

        UserRoleDao userRoleDao = new UserRoleDao();
        boolean hasUserRole = userRoleDao.hasRole(user.getId(), Role.USER, TEST_NOW, em);
        Assertions.assertTrue(hasUserRole);
    }

    @Test
    public void testLoginWithExpiredUserRole() {
        registerTestUserWithRoleExpiry(TEST_NOW, TEST_NOW.minusDays(1));
        Assertions.assertThrows(
                AuthenticationException.class,
                () -> inTransaction(
                        () -> userService.login(
                                TEST_USERNAME,
                                TEST_PWD_1234
                        )
                )
        );
    }

    @Test
    public void testLoginWithInvalidPassword() {
        registerTestUserWithRoleExpiry(TEST_NOW, TEST_NOW.plusDays(10));
        Assertions.assertThrows(
                AuthenticationException.class,
                () -> inTransaction(
                        () -> userService.login(
                                TEST_USERNAME,
                                "wrongPwd"
                        )
                )
        );
    }

    @Test
    public void testLoginWithUnknownUser() {
        registerTestUserWithRoleExpiry(TEST_NOW, TEST_NOW.plusDays(10));
        Assertions.assertThrows(
                AuthenticationException.class,
                () -> inTransaction(
                        () -> userService.login(
                                "unknownUser",
                                TEST_PWD_1234
                        )
                )
        );
    }

    @Test
    public void testLoginWithUserRoleValidInFuture() {
        registerTestUserWithRoleExpiry(TEST_NOW.plusDays(10), TEST_NOW.plusDays(12));
        Assertions.assertThrows(
                AuthenticationException.class,
                () -> inTransaction(
                        () -> userService.login(
                                TEST_USERNAME,
                                TEST_PWD_1234
                        )
                )
        );
    }

    @Test
    public void testLoginWithValidUserRole() {
        User user = registerTestUserWithRoleExpiry(TEST_NOW, TEST_NOW.plusDays(12));

        User login = inTransaction(
                () -> userService.login(
                        TEST_USERNAME,
                        TEST_PWD_1234
                )
        );

        Assertions.assertNotNull(login);

        em.clear();

        User storedUser = em.find(User.class, user.getId());
        Assertions.assertTrue(storedUser.isLoggedIn());
    }

    private User registerTestUserWithRoleExpiry(LocalDateTime from, LocalDateTime to) {
        RegisterUserRequest request = new RegisterUserRequest(
                TEST_USERNAME,
                TEST_PWD_1234,
                TEST_USER_MAIL
        );

        User user = inTransaction(
                () -> userService.register(request)
        );

        inTransaction(() -> {
            UserRoleDao userRoleDao = new UserRoleDao();

            UserRole userRole = userRoleDao.findByUserIdAndRole(
                    user.getId(),
                    Role.USER,
                    em
            );

            userRole.setValidFrom(from);
            userRole.setValidTo(to);

            return userRole;
        });

        return user;
    }

    private <T> T inTransaction(Supplier<T> action) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            T result = action.get();
            tx.commit();
            return result;
        } catch (RuntimeException | Error e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

}
