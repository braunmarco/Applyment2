package my.cvmanager.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import my.cvmanager.domain.Role;
import my.cvmanager.domain.User;
import my.cvmanager.domain.UserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class UserRoleDaoTest {
    private EntityManagerFactory factory;
    private EntityManager entityManager;
    private UserRoleDao userRoleDao;

    @BeforeEach
    public void setUp() {
        factory = Persistence.createEntityManagerFactory("cvmanagerTestPU");
        entityManager = factory.createEntityManager();
        userRoleDao = new UserRoleDao();
    }

    @AfterEach
    public void tearDown() {
        if (entityManager != null) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            entityManager.close();
        }
        if (factory != null) {
            factory.close();
        }
    }

    @Test
    public void testRoleById() {
        User user = new User();
        user.setUsername("marcoAdmin");
        user.setPassword("password");
        user.setEmail("marcoAdmin@email.de");
        UserDao userDao = new UserDao();
        inTransaction(() -> userDao.persist(user, entityManager));

        UserRole userRole = new UserRole();
        userRole.setRole(Role.ADMIN);
        userRole.setUser(user);

        LocalDateTime now = LocalDateTime.of(2026, 10, 12, 12, 0);
        userRole.setValidFrom(now.minusDays(1));
        userRole.setValidTo(now.plusDays(1));

        inTransaction(() -> userRoleDao.persist(userRole, entityManager));
        entityManager.clear();
        boolean hasRole = userRoleDao.hasRole(user.getId(), Role.ADMIN, now, entityManager);

        Assertions.assertTrue(hasRole);
    }

    @Test
    void testExpiredAdminRole() {
        User user = new User();
        user.setUsername("marcoAdmin");
        user.setPassword("password");
        user.setEmail("marcoAdmin@email.de");
        UserDao userDao = new UserDao();
        inTransaction(() -> userDao.persist(user, entityManager));

        UserRole userRole = new UserRole();
        userRole.setRole(Role.ADMIN);
        userRole.setUser(user);

        LocalDateTime now = LocalDateTime.of(2026, 10, 12, 12, 0);
        userRole.setValidFrom(now.minusDays(10));
        userRole.setValidTo(now.minusDays(1));
        inTransaction(() -> userRoleDao.persist(userRole, entityManager));

        entityManager.clear();

        boolean hasRole = userRoleDao.hasRole(user.getId(), Role.ADMIN, now, entityManager);

        Assertions.assertFalse(hasRole);
    }

    @Test
    void testAdminRoleExpiresExactlyNow() {
        User user = new User();
        user.setUsername("marcoAdmin");
        user.setPassword("password");
        user.setEmail("marcoAdmin@email.de");
        UserDao userDao = new UserDao();
        inTransaction(() -> userDao.persist(user, entityManager));

        UserRole userRole = new UserRole();
        userRole.setRole(Role.ADMIN);
        userRole.setUser(user);

        LocalDateTime now = LocalDateTime.of(2026, 10, 12, 12, 0);

        userRole.setValidFrom(now.minusDays(1));
        userRole.setValidTo(now);

        inTransaction(() -> userRoleDao.persist(userRole, entityManager));

        entityManager.clear();

        boolean hasRole = userRoleDao.hasRole(user.getId(), Role.ADMIN, now, entityManager);

        Assertions.assertFalse(hasRole);
    }

    private void inTransaction(Runnable operation) {
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            operation.run();
            transaction.commit();
        } catch (RuntimeException | Error exception) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw exception;
        }
    }

}
