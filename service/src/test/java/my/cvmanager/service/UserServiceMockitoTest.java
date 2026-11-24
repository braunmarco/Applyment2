package my.cvmanager.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import my.cvmanager.domain.Role;
import my.cvmanager.domain.User;
import my.cvmanager.domain.UserRole;
import my.cvmanager.repositories.UserDao;
import my.cvmanager.repositories.UserRoleDao;
import my.cvmanager.service.exception.AuthenticationException;
import my.cvmanager.service.exception.AuthenticationServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class UserServiceMockitoTest {

    @Mock
    UserDao userDao;

    @Mock
    UserRoleDao userRoleDao;

    @Mock
    EntityManager em;

    private UserService userService;

    @Mock
    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userDao, userRoleDao, authorizationService);
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
        User user = TestUserFactory.createUser(1L);
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
     * check login flag while updating the flag a DB error occurs
     *
     * @throws Exception
     */
    @Test
    public void testLoginUpdateDBError() throws Exception {
        User user = TestUserFactory.createUser(1L);
        mockUserLookup(user);

        Mockito.when(userRoleDao.hasRole(
                Mockito.eq(1L),
                Mockito.eq(Role.USER),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(em)
        )).thenReturn(Boolean.TRUE);

        Mockito.when(userDao.update(user, em))
                .thenThrow(new PersistenceException("update has failed."));

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
                "update has failed.",
                exception.getCause().getMessage()
        );

        Assertions.assertTrue(user.isLoggedIn());
        Mockito.verify(userDao, Mockito.times(1))
                .update(user, em);
    }

    /**
     * check user has no valid role
     */
    @Test
    public void testUserHasNoValidRole() {
        User user = TestUserFactory.createUser(1L);
        mockUserLookup(user);

        Mockito.when(userRoleDao.hasRole(
                Mockito.eq(1L),
                Mockito.eq(Role.USER),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(em)
        )).thenReturn(Boolean.FALSE);

        AuthenticationException exception =
                Assertions.assertThrows(
                        AuthenticationException.class,
                        () -> userService.login("testUser", "testPwd1234!")
                );

        Assertions.assertFalse(user.isLoggedIn());

        Mockito.verify(userDao, Mockito.never())
                .update(Mockito.any(User.class), Mockito.eq(em));
    }

    @Test
    public void testWrongUserPassword() {
        User user = TestUserFactory.createUser();
        mockUserLookup(user);

        Assertions.assertThrows(
                AuthenticationException.class,
                () -> userService.login("testUser", "wrongPwd1234!")
        );

        // verify no methods of userRoleDao are called
        Mockito.verifyNoInteractions(userRoleDao);

        // update method is never called
        Mockito.verify(userDao, Mockito.never())
                .update(Mockito.any(User.class), Mockito.eq(em));

        Assertions.assertFalse(user.isLoggedIn());
    }

    /**
     * check login of user with valid role
     */
    @Test
    public void testSucceedLoginWithValidRole() {
        User user = TestUserFactory.createUser(1L);
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
     * verify login with unknown user
     */
    @Test
    public void testLoginWithUnknownUser() {
        Assertions.assertThrows(
                AuthenticationException.class,
                () -> userService.login("someOne", "anyPwd1234!")
        );

        // verify no methods of userRoleDao are called
        Mockito.verifyNoInteractions(userRoleDao);

        // update method is never called
        Mockito.verify(userDao, Mockito.never())
                .update(Mockito.any(User.class), Mockito.eq(em));

        // check findOne called only once
        Mockito.verify(userDao, Mockito.times(1)).findOne(
                Mockito.eq("username"),
                Mockito.eq("someOne"),
                Mockito.eq(em));
    }

    /**
     * Even an administrator must not delete another administrator.
     */
    @Test
    public void testDeleteAdmin() {
        // Arrange: distinguish the acting administrator from the target user.
        Long adminId = 1L;
        Long targetUserId = 2L;

        // The service reads getId(), so the spy must return the target ID.
        User targetUser = TestUserFactory.createUser(targetUserId);

        // Creating a test user does not register it with the mocked DAO.
        Mockito.when(userDao.find(targetUserId, em))
                .thenReturn(targetUser);

        Mockito.when(authorizationService.isAdmin(targetUserId))
                .thenReturn(true);

        // Act & Assert: execute the service and expect the protection exception.
        Assertions.assertThrows(
                IllegalStateException.class,
                () -> userService.deleteUser(adminId, targetUserId)
        );

        // Verify that the acting user's required role was checked.
        Mockito.verify(authorizationService)
                .requireRole(adminId, Role.ADMIN);

        // An exception alone does not prove that delete was never attempted.
        // Verify zero delete calls independently of the exception assertion.
        Mockito.verify(userDao, Mockito.never())
                .delete(Mockito.any(User.class), Mockito.eq(em));
    }

    /**
     * An administrator may delete a non-administrator account.
     */
    @Test
    public void testDeleteUser() {
        // Arrange: distinguish the acting administrator from the target user.
        Long adminId = 1L;
        Long targetUserId = 2L;

        // The service reads getId(), so the spy must return the target ID.
        User targetUser = TestUserFactory.createUser(targetUserId);

        // Creating a test user does not register it with the mocked DAO.
        Mockito.when(userDao.find(targetUserId, em))
                .thenReturn(targetUser);

        Mockito.when(authorizationService.isAdmin(targetUserId))
                .thenReturn(false);

        // Act: execute the service, not the mocked DAO.
        userService.deleteUser(adminId, targetUserId);

        // Verify that the acting user's required role was checked.
        Mockito.verify(authorizationService)
                .requireRole(adminId, Role.ADMIN);

        // Assert: verify(mock) expects exactly one matching invocation.
        Mockito.verify(userDao)
                .delete(targetUser, em);
    }

    /**
     * Verify that an administrator can assign a role to an existing user.
     */
    @Test
    public void testAssignRole() {
        // Arrange: distinguish the acting administrator from the target user.
        Long adminId = 1L;
        Long targetUserId = 2L;

        // No ID spy is needed: assignRole uses the targetUserId parameter.
        User targetUser = TestUserFactory.createUser();

        // Creating a test user does not register it with the mocked DAO.
        Mockito.when(userDao.find(targetUserId, em))
                .thenReturn(targetUser);

        // Act
        userService.assignRole(adminId, targetUserId, Role.ADMIN);

        // Assert: capture the actual role entity passed to persist.
        ArgumentCaptor<UserRole> userCaptor = ArgumentCaptor.forClass(UserRole.class);
        Mockito.verify(userRoleDao).persist(userCaptor.capture(), Mockito.eq(em));

        UserRole savedUserRole = userCaptor.getValue();
        // assertSame checks object identity; assertEquals checks equality.
        Assertions.assertSame(targetUser, savedUserRole.getUser());
        Assertions.assertEquals(Role.ADMIN, savedUserRole.getRole());

        // Verify that the acting user's required role was checked.
        Mockito.verify(authorizationService).requireRole(adminId, Role.ADMIN);
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
}
