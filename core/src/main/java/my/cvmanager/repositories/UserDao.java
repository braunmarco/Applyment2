package my.cvmanager.repositories;

import my.cvmanager.domain.User;

/**
 * Data Access Object (DAO) for {@link User} entities.
 *
 * <p>Inherits standard CRUD operations from {@link BaseDao}.</p>
 *
 * @see BaseDao
 * @see User
 */
public class UserDao extends BaseDao<User> {

    /**
     * Creates a DAO for {@link User} entities.
     */
    public UserDao() {
        super(User.class);
    }

    // more user based methods
}
