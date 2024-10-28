package onion3fusion.domain.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "course")
public class CourseBare {

    @Id private int id;
    private String name;
    private int limit;

    public CourseBare() {

    }

    public CourseBare(int id, String name, int limit) {
        this.id = id;
        this.name = name;
        this.limit = limit;
    }

    public void updateLimitBy(int n) {
        limit += n;
    }

    // for tests (now)
    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public int limit() {
        return limit;
    }

}
