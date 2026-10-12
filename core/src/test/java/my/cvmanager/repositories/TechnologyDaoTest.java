package my.cvmanager.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import my.cvmanager.domain.Technology;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TechnologyDao and its persisted enum level.
 */
public class TechnologyDaoTest {
    private final TechnologyDao dao = new TechnologyDao();

    @Test
    public void shouldCreateAndReadTechnology() {
        Technology technology = newTechnology();
        inTransaction(() -> dao.persist(technology, entityManager));
        assertNotNull(technology.getId());
        entityManager.clear();
        Technology loaded = dao.findBy(technology.getId(), entityManager).orElseThrow();
        assertEquals("Java", loaded.getName());
        assertEquals(Technology.Level.AAA, loaded.getLevel());
    }

    @Test
    public void shouldUpdateTechnologyLevel() {
        Technology technology = newTechnology();
        inTransaction(() -> dao.persist(technology, entityManager));
        Long id = technology.getId();
        entityManager.clear();
        Technology detached = dao.find(id, entityManager);
        entityManager.detach(detached);
        detached.setLevel(Technology.Level.AAAA);
        inTransaction(() -> dao.update(detached, entityManager));
        entityManager.clear();
        assertEquals(Technology.Level.AAAA, dao.find(id, entityManager).getLevel());
    }

    @Test
    public void shouldDeleteTechnology() {
        Technology technology = newTechnology();
        inTransaction(() -> dao.persist(technology, entityManager));
        Long id = technology.getId();
        entityManager.clear();
        Technology detached = dao.find(id, entityManager);
        entityManager.detach(detached);
        inTransaction(() -> dao.delete(detached, entityManager));
        entityManager.clear();
        assertFalse(dao.findBy(id, entityManager).isPresent());
    }

    private Technology newTechnology() {
        Technology technology = new Technology();
        technology.setName("Java");
        technology.setLevel(Technology.Level.AAA);
        return technology;
    }

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
