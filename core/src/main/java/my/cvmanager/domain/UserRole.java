package my.cvmanager.domain;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

public class UserRole {
    Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;
    @Enumerated(EnumType.STRING)
    Role role;
    LocalDateTime validFrom;
    LocalDateTime validTo;
}
