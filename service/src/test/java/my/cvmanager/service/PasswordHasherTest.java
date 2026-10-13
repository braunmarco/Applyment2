package my.cvmanager.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordHasherTest {
    @Test
    public void verifiesCorrectPassword() {
        String encoded = PasswordHasher.hash("a-strong-test-password");
        assertTrue(PasswordHasher.verify("a-strong-test-password", encoded));
        assertFalse(PasswordHasher.verify("wrong-password", encoded));
    }

    @Test
    public void generatesUniqueSaltForEachHash() {
        String first = PasswordHasher.hash("same-password");
        String second = PasswordHasher.hash("same-password");
        assertNotEquals(first, second);
        assertTrue(PasswordHasher.verify("same-password", first));
        assertTrue(PasswordHasher.verify("same-password", second));
    }

    @Test
    public void rejectsPlaintextAndMalformedHashes() {
        assertFalse(PasswordHasher.verify("admin", "admin"));
        assertFalse(PasswordHasher.verify("password", "invalid"));
        assertFalse(PasswordHasher.verify(null, null));
    }

    @Test
    public void rejectsEmptyPassword() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> PasswordHasher.hash(" ")
        );
    }
}
