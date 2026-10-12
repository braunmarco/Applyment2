package my.cvmanager.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.validation.ValidationException;
import my.cvmanager.domain.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserServiceIntegrationTest {
    private EntityManagerFactory emf;
    private EntityManager em;
    private UserService service;

    @Before
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("cvmanagerTestPU");
        em = emf.createEntityManager();
        service = new UserService();
        service.setEntityManager(em);
    }

    @After
    public void tearDown() {
        if (em != null) em.close();
        if (emf != null) emf.close();
    }

    private User register(String username, String password, String email) {
        em.getTransaction().begin();
        try {
            User user = service.register(username, password, email);
            em.getTransaction().commit();
            em.clear();
            return user;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) em.getTransaction().rollback();
            throw e;
        }
    }

    @Test
    public void registrationStoresHashAndCorrectPasswordAllowsLogin() {
        User created = register("tester", "secure-test-password", "tester@example.test");
        User stored = em.find(User.class, created.getId());
        assertNotEquals("secure-test-password", stored.getPassword());
        assertTrue(stored.getPassword().startsWith("pbkdf2-sha256$"));
        assertTrue(PasswordHasher.verify("secure-test-password", stored.getPassword()));

        em.getTransaction().begin();
        User loggedIn = service.login("tester", "secure-test-password");
        em.getTransaction().commit();
        assertNotNull(loggedIn);
        assertTrue(loggedIn.isLoggedIn());
    }

    @Test
    public void wrongPasswordAndUnknownUserCannotLogin() {
        register("tester", "secure-test-password", "tester@example.test");
        em.getTransaction().begin();
        assertNull(service.login("tester", "incorrect"));
        assertNull(service.login("missing", "secure-test-password"));
        em.getTransaction().commit();
    }

    @Test
    public void validateCredentialsUsesHashVerification() {
        register("tester", "secure-test-password", "tester@example.test");
        assertTrue(service.validateCredentials("tester", "secure-test-password"));
        assertFalse(service.validateCredentials("tester", "wrong"));
        assertFalse(service.validateCredentials("missing", "secure-test-password"));
    }

    @Test(expected = ValidationException.class)
    public void duplicateUsernameIsRejected() {
        register("tester", "secure-test-password", "tester@example.test");
        register("tester", "another-password", "other@example.test");
    }

    @Test
    public void passwordChangeReplacesHashAndInvalidatesOldPassword() {
        User created = register("tester", "original-password", "tester@example.test");
        em.getTransaction().begin();
        service.updatePassword(created.getId(), "updated-password");
        em.getTransaction().commit();
        em.clear();
        User stored = em.find(User.class, created.getId());
        assertTrue(PasswordHasher.verify("updated-password", stored.getPassword()));
        assertFalse(PasswordHasher.verify("original-password", stored.getPassword()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void unauthenticatedPasswordResetIsDisabled() {
        service.resetPassword("tester@example.test", "new-password");
    }
}
