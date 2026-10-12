package my.cvmanager.repositories;

import my.cvmanager.domain.UserProfile;

/**
 * Data Access Object (DAO) for {@link UserProfile} entities.
 *
 * <p>Provides standard CRUD operations through the generic
 * {@link BaseDao} implementation.</p>
 *
 * @see BaseDao
 * @see UserProfile
 */
public class UserProfileDao extends BaseDao<UserProfile> {

    /**
     * Creates a DAO for {@link UserProfile} entities.
     */
    public UserProfileDao() {
        super(UserProfile.class);
    }
}
