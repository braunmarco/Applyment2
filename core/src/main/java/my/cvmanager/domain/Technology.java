package my.cvmanager.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "technologies")
public class Technology {

    public enum Level {
        AAAAA("5"),
        AAAA("4"),
        AAA("3"),
        AA("2"),
        A("1"),
        UNKNOWN("");

        String level;

        Level(String level) {
            this.level = level;
        }

        public String code() {
            return level;
        }

        public static Level fromCode(String code) {
            for (Level level : Level.values()) {
                if (level.code().equals(code)) {
                    return level;
                }
            }

            return UNKNOWN;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    //private String level; // "Beginner", "Intermediate", "Advanced"
    @Enumerated(EnumType.STRING)
    private Level level;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Position position;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Technology.Level getLevel() {
        return level;
    }

    public void setLevel(Technology.Level level) {
        this.level = level;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}