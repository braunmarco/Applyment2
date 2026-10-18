package my.cvmanager.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import my.cvmanager.domain.Role;
import my.cvmanager.repositories.UserRoleDao;

import java.time.LocalDateTime;

@ApplicationScoped
public class AuthorizationService {
    @PersistenceContext(unitName = "cvmanagerPU")
    private EntityManager em;

    private final UserRoleDao userRoleDao;

    @Inject
    public AuthorizationService(UserRoleDao userRoleDao) {
        this.userRoleDao = userRoleDao;
    }

    /**
     * Sets the entity manager instance for this class.
     * This method is used for testing purposes to inject a mock entity manager.
     *
     * @param em the entity manager instance to set
     */
    void setEntityManager(EntityManager em) {
        this.em = em;
    }

    public boolean hasRole(Long userId, Role role) {
        return userRoleDao.hasRole(
                userId,
                role,
                //TODO replace  with Clock
                LocalDateTime.now(),
                em
        );
    }

    /**
     * verify user is  admin
     *
     * @param userId
     * @return
     */
    public boolean isAdmin(Long userId) {
        return hasRole(userId, Role.ADMIN);
    }
}
