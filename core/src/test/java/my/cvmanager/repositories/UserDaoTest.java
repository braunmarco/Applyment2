package my.cvmanager.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import my.cvmanager.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for the inherited UserDao CRUD operations.
 */
public class UserDaoTest {
    private final UserDao dao = new UserDao();
    private EntityManagerFactory factory;
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        factory = Persistence.createEntityManagerFactory("cvmanagerTestPU");
        entityManager = factory.createEntityManager();
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
    public void shouldCreateAndReadUser() {
        User user = newUser();
        inTransaction(() -> dao.persist(user, entityManager));
        assertNotNull(user.getId());
        entityManager.clear();
        assertEquals("test-user", dao.findBy(user.getId(), entityManager).orElseThrow().getUsername());
    }

    @Test
    public void shouldUpdateUser() {
        User user = newUser();
        inTransaction(() -> dao.persist(user, entityManager));
        Long id = user.getId();
        entityManager.clear();
        User detached = dao.find(id, entityManager);
        entityManager.detach(detached);
        detached.setEmail("changed@example.invalid");
        inTransaction(() -> dao.update(detached, entityManager));
        entityManager.clear();
        assertEquals("changed@example.invalid", dao.find(id, entityManager).getEmail());
    }

    @Test
    public void shouldDeleteUser() {
        User user = newUser();
        inTransaction(() -> dao.persist(user, entityManager));
        Long id = user.getId();
        entityManager.clear();
        User detached = dao.find(id, entityManager);
        entityManager.detach(detached);
        inTransaction(() -> dao.delete(detached, entityManager));
        entityManager.clear();
        assertFalse(dao.findBy(id, entityManager).isPresent());
    }

    private User newUser() {
        User user = new User();
        user.setUsername("test-user");
        user.setPassword("test-only-password");
        user.setEmail("test@example.invalid");
        return user;
    }

    private void inTransaction(Runnable action) {
        EntityTransaction tx = entityManager.getTransaction();
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
