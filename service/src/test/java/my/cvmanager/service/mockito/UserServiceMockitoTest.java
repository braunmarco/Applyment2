package my.cvmanager.service.mockito;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import my.cvmanager.repositories.UserDao;
import my.cvmanager.repositories.UserRoleDao;
import my.cvmanager.service.UserService;
import my.cvmanager.service.exception.AuthenticationServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class UserServiceMockitoTest {

    @Mock
    UserDao userDao;
    @Mock
    UserRoleDao userRoleDao;
    @Mock
    EntityManager entityManager;

    UserService userService;

    public UserServiceMockitoTest() {
        userService = new UserService(userDao, userRoleDao);
    }

    @Test
    public void testLoginWithValidUserDBError() throws Exception {
        Mockito.when(userDao.findOne(
                Mockito.eq("username"),
                Mockito.eq("testUser"),
                Mockito.any(EntityManager.class)
        )).thenThrow(new PersistenceException("Database unavailable"));

        Assertions.assertThrows(
                AuthenticationServiceException.class,
                () -> userService.login("testUser", "testPwd1234!")
        );
    }
}
