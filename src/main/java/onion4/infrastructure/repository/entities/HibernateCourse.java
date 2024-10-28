package onion4.infrastructure.repository.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import onion4.domain.CourseUpdate;

import java.util.List;

@Entity
@Table(name = "course")
public class HibernateCourse {

    @Id
    private int id;
    private String name;
    private int limit;

    @OneToMany(
            mappedBy = "courseId",
            cascade = CascadeType.ALL,
            // default fetch = LAZY,
            orphanRemoval = true)
    @OrderBy("orderId ASC")
    private List<HibernateEnrollment> enrollments;

    public void apply(CourseUpdate update) {
        update.newLimit().ifPresent(newLimit -> limit = newLimit);
        update.toBeAdded().ifPresent(newEnroll -> enrollments.add(new HibernateEnrollment(id, newEnroll)));
        update.toBeActivated().stream().forEach(toActivate ->
                enrollments.stream()
                        .filter(enrollment -> enrollment.id() == toActivate.id())
                        .forEach(HibernateEnrollment::activate));
        update.toBeRemoved().ifPresent(toRemove -> {
                    enrollments.stream()
                            .filter(enrollment -> enrollment.id() == toRemove.id())
                            .findAny()
                            .ifPresent(hibernateEnrollment -> enrollments.remove(hibernateEnrollment));
                });
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public int limit() {
        return limit;
    }

    public List<HibernateEnrollment> enrollments() {
        return enrollments;
    }

}
