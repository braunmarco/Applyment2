package my.cvmanager.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.AfterClass;
import org.junit.Before;

import java.util.logging.Logger;

public class ServiceTest {
    Logger logger = Logger.getLogger(ServiceTest.class.getName());
    UserService service = new UserService();

    protected static EntityManagerFactory emf;
    protected EntityManager em;

    @Before
    public void setUp() throws Exception {
        emf = Persistence.createEntityManagerFactory("cvmanagerPU");
    }

    @AfterClass
    public static void closeEntityManagerFactory() {
        emf.close();
    }

    /*@Test
    public void testRegisterUser() {
        service.setEntityManager(emf.createEntityManager());
        User user = service.register("admin", "admin", "braun_marco@gmx.de");
        Assert.assertNotNull(user);
    }*/

    /*@Test()
    public void testIsRegistered() {
        service.setEntityManager(emf.createEntityManager());
        boolean registered = service.isUserRegistered("admin", "braun_marco@gmx.de");
        Assert.assertTrue(registered);
    }*/


    /*@Test
    public void testLoginUser() {
        service.setEntityManager(emf.createEntityManager());
        User registered = service.register("admin", "admin", "braun_marco@gmx.de");
        boolean login = service.login("admin", "admin");
        Assert.assertTrue(login);
    }*/
}
