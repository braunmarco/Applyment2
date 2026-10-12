package my.cvmanager.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import my.cvmanager.domain.Position;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for PositionDao CRUD and orderIndex queries.
 */
public class PositionDaoTest {
    private final PositionDao dao = new PositionDao();

    @Test
    public void shouldCreateAndReadPosition() {
        Position position = newPosition("Developer", 2L);
        inTransaction(() -> dao.persist(position, entityManager));
        assertNotNull(position.getId());
        entityManager.clear();
        assertEquals("Developer", dao.findBy(position.getId(), entityManager).orElseThrow().getTitle());
    }

    @Test
    public void shouldUpdatePosition() {
        Position position = newPosition("Developer", 2L);
        inTransaction(() -> dao.persist(position, entityManager));
        Long id = position.getId();
        entityManager.clear();
        Position detached = dao.find(id, entityManager);
        entityManager.detach(detached);
        detached.setTitle("Senior Developer");
        inTransaction(() -> dao.update(detached, entityManager));
        entityManager.clear();
        assertEquals("Senior Developer", dao.find(id, entityManager).getTitle());
    }

    @Test
    public void shouldDeletePosition() {
        Position position = newPosition("Developer", 2L);
        inTransaction(() -> dao.persist(position, entityManager));
        Long id = position.getId();
        entityManager.clear();
        Position detached = dao.find(id, entityManager);
        entityManager.detach(detached);
        inTransaction(() -> dao.delete(detached, entityManager));
        entityManager.clear();
        assertFalse(dao.findBy(id, entityManager).isPresent());
    }

    @Test
    public void shouldSortPositionsInBothDirections() {
        inTransaction(() -> {
            dao.persist(newPosition("Second", 20L), entityManager);
            dao.persist(newPosition("First", 10L), entityManager);
            dao.persist(newPosition("Third", 30L), entityManager);
        });
        entityManager.clear();

        List<Position> ascending = dao.findAllOrderedUp(entityManager);
        assertEquals(3, ascending.size());
        assertEquals("First", ascending.get(0).getTitle());
        assertEquals("Second", ascending.get(1).getTitle());
        assertEquals("Third", ascending.get(2).getTitle());

        List<Position> descending = dao.findAllOrderedDown(entityManager);
        assertEquals(3, descending.size());
        assertEquals("Third", descending.get(0).getTitle());
        assertEquals("Second", descending.get(1).getTitle());
        assertEquals("First", descending.get(2).getTitle());

        assertEquals("First", dao.findAllOrdered(entityManager).get(0).getTitle());
        assertEquals("Third", dao.findAllOrdered(entityManager, PositionDao.Order.DOWN).get(0).getTitle());
    }

    @Test
    public void shouldKeepHashCodeStableAfterPersist() {
        Position position = newPosition("Developer", 1L);
        Set<Position> positions = new HashSet<>();
        positions.add(position);
        int hashBeforePersist = position.hashCode();

        inTransaction(() -> dao.persist(position, entityManager));

        assertNotNull(position.getId());
        assertEquals(hashBeforePersist, position.hashCode());
        assertTrue(positions.contains(position));
    }

    @Test
    public void shouldComparePositionsByPersistedIdentity() {
        Position first = newPosition("Developer", 1L);
        Position second = newPosition("Developer", 1L);
        assertNotEquals(first, second);

        inTransaction(() -> dao.persist(first, entityManager));
        Long id = first.getId();
        entityManager.clear();

        Position reloaded = dao.find(id, entityManager);
        assertNotSame(first, reloaded);
        assertEquals(first, reloaded);
        assertEquals(first.hashCode(), reloaded.hashCode());
        assertNotEquals(first, second);
    }

    private Position newPosition(String title, long orderIndex) {
        Position position = new Position();
        position.setTitle(title);
        position.setCompany("Test Company");
        position.setOrderIndex(orderIndex);
        return position;
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
