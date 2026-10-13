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
        em.getTransaction().begin();
        User user = userService.register(
                "testUser",
                "testPwd1234!",
                "testUser@example.com");
        em.getTransaction().commit();

        Assertions.assertNotNull(user.getId());

        em.clear();

        UserRoleDao userRoleDao = new UserRoleDao();
        boolean hasUserRole = userRoleDao.hasRole(user.getId(), Role.USER, LocalDateTime.now(), em);
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
