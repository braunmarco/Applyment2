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

    public boolean hasRole(Long userId, Role role) {
        return userRoleDao.hasRole(
                userId,
                role,
                //TODO replace  with Clock
                LocalDateTime.now(),
                em
        );
    }

    //TODO create  method to verify  user is admin
}
