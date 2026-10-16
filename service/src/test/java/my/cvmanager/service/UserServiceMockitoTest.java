package my.cvmanager.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import my.cvmanager.domain.Role;
import my.cvmanager.domain.User;
import my.cvmanager.repositories.UserDao;
import my.cvmanager.repositories.UserRoleDao;
import my.cvmanager.service.exception.AuthenticationException;
import my.cvmanager.service.exception.AuthenticationServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

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

    /**
     * verify valid userRole when DBError
     *
     * @throws Exception
     */
    @Test
    public void testCheckValidUserRoleDBError() throws Exception {
        User user = createTestUser();
        mockUserLookup(user);

        Mockito.when(userRoleDao.hasRole(
                Mockito.eq(1L),
                Mockito.eq(Role.USER),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(em)
        )).thenThrow(new PersistenceException("Database unavailable"));

        AuthenticationServiceException exception =
                Assertions.assertThrows(
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

    /**
     * check user has no valid role
     */
    @Test
    public void testUserHasNoValidRole() {
        User user = createTestUser();
        mockUserLookup(user);

        Mockito.when(userRoleDao.hasRole(
                Mockito.eq(1L),
                Mockito.eq(Role.USER),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(em)
        )).thenReturn(Boolean.FALSE);

        AuthenticationException exception = Assertions.assertThrows(
                AuthenticationException.class,
                () -> userService.login("testUser", "testPwd1234!")
        );

        Assertions.assertFalse(user.isLoggedIn());

        Mockito.verify(userDao, Mockito.never())
                .update(Mockito.any(User.class), Mockito.eq(em));
    }

    /**
     * check login of user with valid role
     */
    @Test
    public void testSucceedLoginWithValidRole() {
        User user = createTestUser();
        mockUserLookup(user);

        Mockito.when(userRoleDao.hasRole(
                Mockito.eq(1L),
                Mockito.eq(Role.USER),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(em)
        )).thenReturn(Boolean.TRUE);

        Mockito.when(userDao.update(user, em))
                .thenReturn(user);

        User result = userService.login("testUser", "testPwd1234!");
        Assertions.assertSame(user, result);
        Assertions.assertTrue(user.isLoggedIn());
        Mockito.verify(userDao, Mockito.times(1)).update(user, em);
    }

    /**
     * creates test user for mockito tests
     *
     * @return a test user with a predefined ID and hashed password
     */
    private User createTestUser() {
        User user = Mockito.spy(new User());

        user.setUsername("testUser");
        user.setPassword(PasswordHasher.hash("testPwd1234!"));

        Mockito.doReturn(1L)
                .when(user)
                .getId();

        return user;
    }

    /**
     * find mocked user
     *
     * @param user the user returned by the mocked DAO
     */
    private void mockUserLookup(User user) {
        Mockito.when(userDao.findOne(
                Mockito.eq("username"),
                Mockito.eq("testUser"),
                Mockito.eq(em)
        )).thenReturn(user);
    }

    @Test
    void shouldInitializeMocks() {
        assertNotNull(userDao);
        assertNotNull(userRoleDao);
        assertNotNull(em);
    }
}
