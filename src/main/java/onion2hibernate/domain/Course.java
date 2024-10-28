package onion2hibernate.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Entity
public class Course {

    private static final Logger logger = LoggerFactory.getLogger(Course.class);

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
    private List<Enrollment> enrollments;

    public Course() {

    }

    public Course(int id, String name, int limit, List<Enrollment> enrollments) {
        this.id = id;
        this.name = name;
        this.limit = limit;
        this.enrollments = enrollments;
    }

    public String enroll(int studentId) {
        Optional<Enrollment> alreadyEnrolled = enrollments.stream()
                .filter(e -> e.studentId() == studentId)
                .findFirst();
        if (alreadyEnrolled.isPresent()) {
            logger.info("Duplication");
            return alreadyEnrolled.get().status();
        }

        int orderId = enrollments.stream()
                .mapToInt(Enrollment::orderId)
                .max()
                .orElse(0) + 1;
        long activeCount = enrollments.stream().filter(e -> e.status().equals("A")).count();
        String status = activeCount < limit ? "A" : "W";
        enrollments.add(new Enrollment(id, studentId, orderId, status));

        logger.info("Student {} enrolled to {} with status {}", studentId, name, status);

        return status;
    }

    public Optional<Notification> leave(int studentId) {
        Optional<Enrollment> alreadyEnrolled = enrollments.stream()
                .filter(e -> e.studentId() == studentId)
                .findFirst();
        if (alreadyEnrolled.isEmpty()) {
            logger.info("Duplication");
            return Optional.empty();
        }
        Enrollment leaving = alreadyEnrolled.get();
        enrollments.remove(leaving);
        if (leaving.isWaiting()) {
            return Optional.empty();
        }
        Optional<Enrollment> firstWaiting = enrollments.stream()
                .filter(Enrollment::isWaiting)
                .findFirst();
        // leaving is active
        if (firstWaiting.isEmpty()) {
            return Optional.empty();
        } else {
            firstWaiting.get().activate();
            return Optional.of(activationNotification(firstWaiting.get()));
        }
    }

    public List<Notification> updateLimitBy(int n) {
        limit += n;
        return enrollments
                .stream()
                .filter(Enrollment::isWaiting)
                .limit(n)
                .peek(Enrollment::activate)
                .map(this::activationNotification)
                .toList();

    }

    private Notification activationNotification(Enrollment enrollment) {
        return new Notification(id, enrollment.studentId(), "A");
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

    public List<Enrollment> enrollments() {
        return enrollments;
    }
}
