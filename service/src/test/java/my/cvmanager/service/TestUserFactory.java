package my.cvmanager.service;

import my.cvmanager.domain.User;
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
     * creates test user for mockito tests
     *
     * @param id
     * @return a test user with a predefined ID and hashed password
     */
    public static User createUser(Long id) {
        User user = Mockito.spy(createUser());
        Mockito.doReturn(id).when(user).getId();

        return user;
    }
}
