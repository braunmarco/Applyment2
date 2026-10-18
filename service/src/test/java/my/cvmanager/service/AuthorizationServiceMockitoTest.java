package my.cvmanager.service;

import jakarta.persistence.EntityManager;
import my.cvmanager.domain.Role;
import my.cvmanager.repositories.UserRoleDao;
import my.cvmanager.service.exception.AuthorizationException;
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

        Mockito.when(userRoleDao.hasRole(
                Mockito.eq(1L),
                Mockito.eq(Role.ADMIN),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(em)
        )).thenReturn(true);

        boolean result = authorizationService.isAdmin(1L);

        Assertions.assertTrue(result);
    }

    @Test
    void testRequireRoleThrowsExceptionWhenRoleMissing() {
        Long userId = 1L;

        Mockito.when(userRoleDao.hasRole(
                Mockito.eq(userId),
                Mockito.eq(Role.ADMIN),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(em)
        )).thenReturn(false);

        AuthorizationException exception = Assertions.assertThrows(
                AuthorizationException.class,
                () -> authorizationService.requireRole(
                        userId, Role.ADMIN
                )
        );

        Assertions.assertEquals(
                "Access denied: required role ADMIN",
                exception.getMessage()
        );
    }

    @Test
    void testRequireRoleSucceedsWhenRoleExists() {
        Long userId = 1L;

        Mockito.when(userRoleDao.hasRole(
                Mockito.eq(userId),
                Mockito.eq(Role.ADMIN),
                Mockito.any(LocalDateTime.class),
                Mockito.eq(em)
        )).thenReturn(true);

        Assertions.assertDoesNotThrow(
                () -> authorizationService.requireRole(
                        userId, Role.ADMIN
                )
        );
    }
}
