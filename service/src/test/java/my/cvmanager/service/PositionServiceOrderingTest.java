package my.cvmanager.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import my.cvmanager.domain.Position;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PositionServiceOrderingTest {
    private EntityManagerFactory emf;
    private EntityManager em;
    private PositionService service;

    @Before
    public void setUp() throws Exception {
        emf = Persistence.createEntityManagerFactory("cvmanagerTestPU");
        em = emf.createEntityManager();
        service = new PositionService();
        Field field = PositionService.class.getDeclaredField("em");
        field.setAccessible(true);
        field.set(service, em);
    }

    @After
    public void tearDown() {
        if (em != null) em.close();
        if (emf != null) emf.close();
    }

    private Position persist(String title, long index) {
        Position position = new Position();
        position.setTitle(title);
        position.setOrderIndex(index);
        em.persist(position);
        return position;
    }

    private void seed() {
        em.getTransaction().begin();
        // Insertion order intentionally differs from display order.
        persist("Third", 2);
        persist("First", 0);
        persist("Second", 1);
        em.getTransaction().commit();
        em.clear();
    }

    private void assertOrder(String... titles) {
        List<Position> positions = service.findAllOrderedUp();
        assertEquals(titles.length, positions.size());
        for (int i = 0; i < titles.length; i++) {
            assertEquals(titles[i], positions.get(i).getTitle());
            assertEquals(Long.valueOf(i), positions.get(i).getOrderIndex());
        }
    }

    @Test
    public void moveDownUsesOrderIndexRatherThanInsertionOrder() {
        seed();
        em.getTransaction().begin();
        Position first = service.findAllOrderedUp().get(0);
        service.moveDown(first);
        em.getTransaction().commit();
        em.clear();
        assertOrder("Second", "First", "Third");
    }

    @Test
    public void moveUpUsesOrderIndexRatherThanInsertionOrder() {
        seed();
        em.getTransaction().begin();
        Position third = service.findAllOrderedUp().get(2);
        service.moveUp(third);
        em.getTransaction().commit();
        em.clear();
        assertOrder("First", "Third", "Second");
    }

    @Test
    public void boundaryMovesDoNotChangeOrder() {
        seed();
        em.getTransaction().begin();
        List<Position> ordered = service.findAllOrderedUp();
        service.moveUp(ordered.get(0));
        service.moveDown(ordered.get(2));
        em.getTransaction().commit();
        em.clear();
        assertOrder("First", "Second", "Third");
    }
}
