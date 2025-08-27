package my.cvmanager.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "technologies")
public class Technology {

    public enum Level {
        AAA("AAA"),
        AA("AA"),
        A("A");

        String level;

        Level(String level) {
            this.level = level;
        }

        public String code() {
            return level;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String level; // "Beginner", "Intermediate", "Advanced"

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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}