package my.cvmanager.repositories;

import my.cvmanager.domain.User;

public class UserDao extends BaseDao<User> {

    public UserDao() {
        super(User.class);
    }

    // more user based methods
}
