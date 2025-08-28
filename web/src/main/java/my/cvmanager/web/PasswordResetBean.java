package my.cvmanager.web;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import my.cvmanager.service.UserService;

import java.io.Serializable;

/**
 * Backing bean for handling password reset functionality in JSF views.
 * <p>
 * This bean is {@link ViewScoped}, meaning it lives as long as the user is
 * interacting with the same JSF view. It is responsible for collecting
 * user input (email and new password), validating it, and delegating
 * the actual password reset operation to the {@link UserService}.
 * </p>
 */
@Named("passwordResetBean")
@ViewScoped
public class PasswordResetBean implements Serializable {

    private String resetEmail;
    private String newPassword;
    private String confirmPassword;

    @Inject
    private UserService userService;

    /**
     * Initializes the bean when the view is first loaded.
     * Clears all form fields to ensure that the password form
     * is empty when the page is opened.
     */
    @jakarta.annotation.PostConstruct
    public void init() {
        clearForm();
        // Executed only on the initial GET request, not on postbacks
        if (!FacesContext.getCurrentInstance().isPostback()) {
            // Additional initialization can be done here if needed
        }
    }

    /**
     * Handles the password reset form submission.
     * <p>
     * Validates that the entered passwords match. If validation passes,
     * delegates the reset to {@link #resetPasswordByEmail(String, String)}.
     * If validation fails, adds a warning message to the FacesContext.
     * </p>
     *
     * @return navigation outcome, or {@code null} to stay on the same page
     */
    public String resetPasswordByEmail() {
        try {
            if (newPassword == null || !newPassword.equals(confirmPassword)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Passwords do not match.", null));
                return null; // stay on the same view
            }

            return resetPasswordByEmail(resetEmail, newPassword);

        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error while resetting password.", null));
        }
        return null; // stay on the same page
    }

    /**
     * Performs the actual password reset by calling the {@link UserService}.
     * Adds a success message to the context and redirects the user to the login page.
     *
     * @param email          the user's email address
     * @param newRawPassword the new raw password (to be hashed by the service)
     * @return navigation string to the login page with redirect
     */
    public String resetPasswordByEmail(String email, String newRawPassword) {
        userService.resetPassword(email, newRawPassword);

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Password was successfully changed.", null));

        return "login.xhtml?faces-redirect=true";
    }

    /**
     * Clears all form fields (email, password, confirmPassword).
     * Useful when initializing the form or after a successful reset.
     */
    private void clearForm() {
        resetEmail = "";
        newPassword = "";
        confirmPassword = "";
    }

    // Getters / Setters

    /**
     * @return the email entered by the user
     */
    public String getResetEmail() {
        return resetEmail;
    }

    public void setResetEmail(String resetEmail) {
        this.resetEmail = resetEmail;
    }

    /**
     * @return the new password entered by the user
     */
    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    /**
     * @return the confirmed password entered by the user
     */
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
