package my.cvmanager.web;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.validation.ValidationException;
import my.cvmanager.domain.User;
import my.cvmanager.service.UserService;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * UserBean class for managing users.
 */
@Named("userBean")
@SessionScoped
public class UserBean implements Serializable {
    /**
     * The logger instance for this class.
     */
    private final Logger logger = Logger.getLogger(UserBean.class.getName());

    private String username;
    private String password;
    private String email;

    private User loggedInUser;

    @Inject
    private UserService userService;

    private String confirmPassword;

    /**
     * Registers a new user.
     *
     * @return the URL of the home page
     */
    public String register() {
        try {
            loggedInUser = userService.register(username, password, email);
            clearForm();

            return "home.xhtml?faces-redirect=true";
        } catch (ValidationException e) {
            // Error handling
            return "register.xhtml?error=true";
        }
    }

    /**
     * Unregisters a user.
     *
     * @return the URL of the login page
     */
    public String unregister() {
        try {
            userService.unregister(loggedInUser.getId());
            loggedInUser = null;

            return "login.xhtml?faces-redirect=true";
        } catch (Exception e) {
            // Error handling
            return "unregister.xhtml?error=true";
        }
    }

    /**
     * Logs in a user.
     *
     * @return the URL of the home page
     */
    public String login() {
        try {
            User user = userService.login(username, password);
            if (user != null) {
                loggedInUser = user;

                return "home.xhtml?faces-redirect=true";
            } else { // TODO FaceletMessage
                throw new AuthenticationException("Login failed");
            }
        } catch (AuthenticationException e) {
            // Error handling
            return "login.xhtml?error=true";
        }
    }

    /**
     * Logs out a user.
     *
     * @return the URL of the login page
     */
    public String logout() {
        if (loggedInUser != null) {
            userService.logout(loggedInUser.getId());
            loggedInUser = null;
        }
        return "login.xhtml?faces-redirect=true";
    }

    public String forgotPassword() {
        // Hier prüfen, ob Username oder Mail existiert
        // Dann Mail mit Reset-Link oder temporärem Passwort senden
        // z.B. MailService.sendPasswordReset(user.getEmail());

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Anweisungen zum Zurücksetzen des Passworts wurden gesendet.",
                        null));

        return "resetPassword.xhtml?faces-redirect=true";
    }

    private boolean checkPasswords(String pass, String confirmedPass) {
        if (!pass.equals(confirmedPass)) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Die Passwörter stimmen nicht überein.", null));
            return false;
        } else {
            return true;
        }
    }

    // Hilfsmethode
    private void clearForm() {
        username = null;
        password = null;
        email = null;
    }

    // Getter & Setter
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
