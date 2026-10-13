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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.function.Supplier;

public class UserServiceTest {
    //private Logger logger = Logger.getLogger(UserServiceTest.class.getName());
    private LocalDateTime TEST_NOW = LocalDateTime.now();
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
        RegisterUserRequest registerUserRequest = new RegisterUserRequest("testUser", "testPwd1234!", "testUser@example.com");
        User user = inTransaction(() -> userService.register(registerUserRequest));
        Assertions.assertNotNull(user.getId());

        em.clear();

        UserRoleDao userRoleDao = new UserRoleDao();
        boolean hasUserRole = userRoleDao.hasRole(user.getId(), Role.USER, TEST_NOW, em);
        Assertions.assertTrue(hasUserRole);
    }

    @Test
    public void testLoginWithExpiredUserRole() {
        RegisterUserRequest request =
                new RegisterUserRequest(
                        "testUser",
                        "testPwd1234!",
                        "testUser@example.com"
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

            userRole.setValidTo(
                    LocalDateTime.now().minusDays(1)
            );

            return userRole;
        });

        User login = inTransaction(
                () -> userService.login(
                        request.username(),
                        request.password()
                )
        );
        Assertions.assertNull(login);
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
