package my.cvmanager.service;

import jakarta.persistence.EntityManager;
import my.cvmanager.domain.Role;
import my.cvmanager.domain.User;
import my.cvmanager.repositories.UserRoleDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
public class AuthorizationServiceMockitoTest {

    @Mock
    private EntityManager em;

    @Mock
    private UserRoleDao userRoleDao;

    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        authorizationService = new AuthorizationService(userRoleDao);
        authorizationService.setEntityManager(em);
    }

    /**
     * check user has no valid role
     */
    @Test
    public void testUserHasRole() {
        User user = TestUserFactory.createUser(1L);

        Mockito.when(userRoleDao.hasRole(
                Mockito.eq(1L),
                Mockito.eq(Role.USER),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(em)
        )).thenReturn(true);

        boolean result = authorizationService.hasRole(1L, Role.USER);

        Assertions.assertTrue(result);
    }
}
