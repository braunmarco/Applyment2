package my.cvmanager.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import my.cvmanager.domain.UserProfile;
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
