package my.cvmanager.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import my.cvmanager.domain.Role;
import my.cvmanager.domain.UserRole;

import java.time.LocalDateTime;

public class UserRoleDao extends BaseDao<UserRole> {
    public UserRoleDao() {
        super(UserRole.class);
    }

    public boolean hasRole(Long userId, Role role, LocalDateTime now, EntityManager entityManager) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(ur)\n" +
                        "FROM UserRole ur\n" +
                        "WHERE ur.user.id = :userId\n" +
                        "AND ur.role = :role\n" +
                        "AND (ur.validFrom IS NULL OR ur.validFrom <= :now)\n" +
                        "AND (ur.validTo IS NULL OR ur.validTo > :now)", Long.class
        );

        query.setParameter("userId", userId);
        query.setParameter("role", role);
        query.setParameter("now", now);

        Long cnt = query.getSingleResult();

        return cnt > 0;
    }

    public UserRole findByUserIdAndRole(Long userId, Role role, EntityManager entityManager) {
        TypedQuery<UserRole> query = entityManager.createQuery(
                "SELECT ur from UserRole ur\n" +
                        "WHERE ur.user.id = :userId\n" +
                        "AND ur.role = :role", UserRole.class
        );

        query.setParameter("userId", userId);
        query.setParameter("role", role);

        return query.getSingleResult();
    }
}
