package my.cvmanager.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import my.cvmanager.domain.Role;
import my.cvmanager.domain.User;
import my.cvmanager.repositories.UserRoleDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

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
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("testPwd1234!");
        user.setEmail("testUser@example.com");

        inTransaction(() -> userService.register(user));
        Assertions.assertNotNull(user.getId());

        em.clear();

        UserRoleDao userRoleDao = new UserRoleDao();
        boolean hasUserRole = userRoleDao.hasRole(user.getId(), Role.USER, TEST_NOW, em);
        Assertions.assertTrue(hasUserRole);
    }

    private void inTransaction(Runnable action) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        try {
            action.run();
            tx.commit();
        } catch (RuntimeException | Error e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

}
