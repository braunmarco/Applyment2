package my.cvmanager.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import my.cvmanager.domain.Role;
import my.cvmanager.domain.UserRole;

import java.time.LocalDateTime;

public class UserRoleDao extends BaseDao<UserRole> {
    public UserRoleDao() {
        super(UserRole.class);
    }

    public boolean hasRole(Long userId, Role role, LocalDateTime now, EntityManager entityManager) {
        Query query = entityManager.createNativeQuery(
                "SELECT COUNT(ur)\n" +
                        "FROM UserRole ur\n" +
                        "WHERE ur.user.id = :userId\n" +
                        "AND ur.role = :role\n" +
                        "AND (ur.validFrom IS NULL OR ur.validFrom <= :now)\n" +
                        "AND (ur.validTo IS NULL OR ur.validTo > :now)"
        );

        long cnt = ((Number) query.getSingleResult()).longValue();

        return cnt > 0;
    }
}
