package my.cvmanager.repositories;

import my.cvmanager.domain.Role;
import my.cvmanager.domain.UserRole;

import java.time.LocalDateTime;

public class UserRoleDao extends BaseDao<UserRole> {
    public UserRoleDao() {
        super(UserRole.class);
    }

    public boolean hasRole(Long userId, Role role, LocalDateTime now) {
        return false;
    }
}
