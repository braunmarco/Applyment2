package my.cvmanager.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import my.cvmanager.domain.UserProfile;
import my.cvmanager.domain.Position;
import my.cvmanager.domain.Technology;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Integration tests for the inherited CRUD operations of {@link UserProfileDao}.
 * Uses a separate in-memory H2 database instead of the application datasource.
 */
public class UserProfileDaoTest {

    private EntityManagerFactory factory;
    private EntityManager entityManager;
    private UserProfileDao dao;

    @Before
    public void setUp() {
        factory = Persistence.createEntityManagerFactory("cvmanagerTestPU");
        entityManager = factory.createEntityManager();
        dao = new UserProfileDao();
    }

    @After
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
    public void shouldCreateAndReadUserProfile() {
        UserProfile profile = newProfile();
        inTransaction(() -> dao.persist(profile, entityManager));

        assertNotNull(profile.getId());
        entityManager.clear();

        Optional<UserProfile> loaded = dao.findBy(profile.getId(), entityManager);
        assertTrue(loaded.isPresent());
        assertEquals("Test", loaded.get().getFirstName());
        assertEquals("User", loaded.get().getLastName());
    }

    @Test
    public void shouldUpdateUserProfile() {
        UserProfile profile = newProfile();
        inTransaction(() -> dao.persist(profile, entityManager));
        Long id = profile.getId();
        entityManager.clear();

        UserProfile detached = dao.find(id, entityManager);
        entityManager.detach(detached);
        detached.setFirstName("Updated");

        inTransaction(() -> dao.update(detached, entityManager));
        entityManager.clear();

        assertEquals("Updated", dao.find(id, entityManager).getFirstName());
    }

    @Test
    public void shouldDeleteUserProfile() {
        UserProfile profile = newProfile();
        inTransaction(() -> dao.persist(profile, entityManager));
        Long id = profile.getId();
        entityManager.clear();

        UserProfile detached = dao.find(id, entityManager);
        entityManager.detach(detached);
        inTransaction(() -> dao.delete(detached, entityManager));
        entityManager.clear();

        assertFalse(dao.findBy(id, entityManager).isPresent());
    }

    @Test
    public void shouldCascadePersistProfileWithPositionAndTechnology() {
        UserProfile profile = newProfile();
        Position position = new Position();
        position.setTitle("Software Developer");
        position.setOrderIndex(1L);
        Technology technology = new Technology();
        technology.setName("Java");
        technology.setLevel(Technology.Level.AAA);

        position.addTechnology(technology);
        profile.addPosition(position);

        assertSame(profile, position.getUser());
        assertSame(position, technology.getPosition());

        inTransaction(() -> dao.persist(profile, entityManager));
        Long profileId = profile.getId();
        assertNotNull(position.getId());
        assertNotNull(technology.getId());

        entityManager.clear();
        UserProfile loaded = dao.findBy(profileId, entityManager).orElseThrow();
        assertEquals(1, loaded.getPositions().size());

        Position loadedPosition = loaded.getPositions().get(0);
        assertEquals("Software Developer", loadedPosition.getTitle());
        assertEquals(profileId, loadedPosition.getUser().getId());
        assertEquals(1, loadedPosition.getTechnologies().size());

        Technology loadedTechnology = loadedPosition.getTechnologies().get(0);
        assertEquals("Java", loadedTechnology.getName());
        assertEquals(Technology.Level.AAA, loadedTechnology.getLevel());
        assertEquals(loadedPosition.getId(), loadedTechnology.getPosition().getId());
    }

    private UserProfile newProfile() {
        UserProfile profile = new UserProfile();
        profile.setFirstName("Test");
        profile.setLastName("User");
        profile.setEmail("test@example.invalid");
        return profile;
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
