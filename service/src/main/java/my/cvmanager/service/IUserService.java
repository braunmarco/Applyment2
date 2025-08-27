package my.cvmanager.service;

import my.cvmanager.domain.User;

public interface IUserService {
    User register(String username, String password, String email);

    void unregister(Long userId);

    User login(String username, String password);

    void logout(Long userId);

    boolean validateCredentials(String username, String password);

    void sendCredentials(String email);

    User isUserRegistered(String username, String email);
}
