package my.cvmanager.web.validator;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.FacesValidator;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

/**
 * Validates that the value of the current component (e.g., "confirm password")
 * matches another value (e.g., "new password").
 * <p>
 * Usage in XHTML:
 * <p:password id="newPassword" value="#{userBean.newPassword}" .../>
 * <p:password id="confirmPassword" value="#{userBean.confirmPassword}" ...>
 * <f:validator validatorId="confirmPasswordValidator"/>
 * <f:attribute name="otherValue" value="#{userBean.newPassword}"/>
 * </p:password>
 * <p>
 * The validator reads the "otherValue" attribute (must be a String) and compares it
 * to the submitted/converted value of the component it validates.
 */
@FacesValidator("confirmPasswordValidator")
public class ConfirmPasswordValidator implements Validator<Object> {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value)
            throws ValidatorException {

        // current field's value (e.g. confirmPassword)
        String confirm = value != null ? value.toString() : "";

        // the "other" value passed via <f:attribute name="otherValue" .../>
        Object other = component.getAttributes().get("otherValue");
        String original = other != null ? other.toString() : "";

        if (!confirm.equals(original)) {
            FacesMessage msg = new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Die Passwörter stimmen nicht überein.",
                    "Bitte gib zweimal dasselbe Passwort ein."
            );
            throw new ValidatorException(msg);
        }
    }
}
