package my.cvmanager.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class UserServiceTest {
    //private Logger logger = Logger.getLogger(UserServiceTest.class.getName());
    private EntityManagerFactory emf;
    private EntityManager em;
    private UserService service;

    @BeforeEach
    public void setUp() {
        emf = Persistence.createEntityManagerFactory("cvmanagerTestPU");
        em = emf.createEntityManager();
        service = new UserService();
        service.setEntityManager(em);
    }

    @AfterEach
    public void tearDown() {
        if (em != null) em.close();
        if (emf != null) emf.close();
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
