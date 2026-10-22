package my.cvmanager.service;

import jakarta.persistence.EntityManager;
import my.cvmanager.domain.Role;
import my.cvmanager.domain.User;
import my.cvmanager.domain.UserRole;
import my.cvmanager.repositories.UserDao;
import my.cvmanager.repositories.UserRoleDao;
import org.mockito.Mockito;

public final class TestUserFactory {
    private static final String USERNAME = "testUser";
    private static final String PASSWORD = "testPwd1234!";

    private TestUserFactory() {
    }

    /**
     * creates test user for mockito tests
     *
     * @return a test user without a predefined ID and hashed password
     */
    public static User createUser() {
        User user = new User();
        user.setUsername(USERNAME);
        user.setPassword(PasswordHasher.hash(PASSWORD));

        return user;
    }

    /**
     * Creates a test user with a predefined ID.
     *
     * @param id the ID returned by the user spy
     * @return a test user with the specified ID
     */
    public static User createUser(Long id) {
        User user = Mockito.spy(createUser());
        Mockito.doReturn(id).when(user).getId();

        return user;
    }

    public static User createAdminUser(EntityManager em) {
        User adminUser = createUser();
        adminUser.setUsername("admin");
        adminUser.setPassword(PasswordHasher.hash("admin"));

        UserRole userRole = new UserRole();
        UserRoleDao userRoleDao = new UserRoleDao();
        userRole.setUser(adminUser);
        userRole.setRole(Role.ADMIN);

        UserDao userDao = new UserDao();
        userDao.persist(adminUser, em);

        //AuthorizationService authorizationService = new AuthorizationService(userRoleDao);
        //UserService userService = new UserService(userDao, userRoleDao, authorizationService);
        //userService.assignRole();

        return adminUser;
    }
}
