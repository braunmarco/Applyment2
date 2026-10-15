package my.cvmanager.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import my.cvmanager.repositories.UserDao;
import my.cvmanager.repositories.UserRoleDao;
import my.cvmanager.service.exception.AuthenticationServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockitoTest {

    @Mock
    UserDao userDao;

    @Mock
    UserRoleDao userRoleDao;

    @Mock
    EntityManager em;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userDao, userRoleDao);
        userService.setEntityManager(em);
    }

    /**
     * check DB connection and login
     **/
    @Test
    public void testLoginWithValidUserDBError() throws Exception {
        Mockito.when(userDao.findOne(
                Mockito.eq("username"),
                Mockito.eq("testUser"),
                Mockito.any(EntityManager.class)
        )).thenThrow(new PersistenceException("Database unavailable"));

        AuthenticationServiceException exception = Assertions.assertThrows(
                AuthenticationServiceException.class,
                () -> userService.login("testUser", "testPwd1234!")
        );

        Assertions.assertInstanceOf(
                PersistenceException.class,
                exception.getCause()
        );

        Assertions.assertEquals(
                "Database unavailable",
                exception.getCause().getMessage()
        );
    }

    @Test
    void shouldInitializeMocks() {
        assertNotNull(userDao);
        assertNotNull(userRoleDao);
        assertNotNull(em);
    }
}
