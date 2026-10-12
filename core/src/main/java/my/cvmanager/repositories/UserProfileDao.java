package my.cvmanager.repositories;

import my.cvmanager.domain.UserProfile;

public class UserProfileDao extends BaseDao<UserProfile> {
    public UserProfileDao() {
        super(UserProfile.class);
    }
}
