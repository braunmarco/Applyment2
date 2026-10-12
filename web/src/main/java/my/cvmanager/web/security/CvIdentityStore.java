package my.cvmanager.web.security;

import jakarta.security.enterprise.credential.Credential;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import jakarta.security.enterprise.identitystore.IdentityStore;

public class CvIdentityStore implements IdentityStore {
    @Override
    public CredentialValidationResult validate(Credential credential) {
        return IdentityStore.super.validate(credential);
    }
}
