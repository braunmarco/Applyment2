package my.cvmanager.service;

import jakarta.persistence.EntityManager;
import my.cvmanager.domain.Role;
import my.cvmanager.domain.User;
import my.cvmanager.repositories.UserDao;
import my.cvmanager.repositories.UserRoleDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class AuthorizationServiceMockitoTest {

    @Mock
    private EntityManager em;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    UserDao userDao;

    @Mock
    private UserRoleDao userRoleDao;

    @BeforeEach
    void setUp() {
        authorizationService = new AuthorizationService(userRoleDao);
    }

    /**
     * check user has no valid role
     */
    @Test
    public void testUserHasRole() {
        User user = TestUserFactory.createUser(1L);
        mockUserLookup(user);

        Mockito.when(authorizationService.hasRole(
                user.getId(),
                Role.ADMIN)
        ).thenReturn(Boolean.FALSE);
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
