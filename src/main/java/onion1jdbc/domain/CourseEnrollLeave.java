package onion1jdbc.domain;

import onion1jdbc.domain.CourseUpdate.NewEnrollment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CourseEnrollLeave {

    private static final Logger logger = LoggerFactory.getLogger(CourseEnrollLeave.class);

    private final int id;
    private final String name;
    private final int limit;
    private final int occupied;
    private final Optional<Enrollment> existing;
    private final Optional<Enrollment> firstWaiting;
    private final int nextOrderId;

    public CourseEnrollLeave(int id, String name, int limit, int occupied, Optional<Enrollment> existing, Optional<Enrollment> firstWaiting, int nextOrderId) {
        this.id = id;
        this.name = name;
        this.limit = limit;
        this.occupied = occupied;
        this.existing = existing;
        this.firstWaiting = firstWaiting;
        this.nextOrderId = nextOrderId;
    }

    public EnrollResult enroll(int studentId) {

        if (existing.isPresent()) {
            logger.info("Duplication");
            return new EnrollResult(existing.get().status());
        }

        String status = occupied < limit ? "A" : "W";

        NewEnrollment newEnrollment = new NewEnrollment(studentId, nextOrderId, status);
        CourseUpdate courseUpdate = CourseUpdate.justAdd(id, newEnrollment);

        logger.info("Student {} enrolled to {} with status {}", studentId, name, status);

        return new EnrollResult(status, courseUpdate);
    }

    public LeaveResult leaveExisting() {
        if (existing.isEmpty()) {
            logger.info("Duplication");
            return LeaveResult.noop();
        }

        Enrollment leaving = existing.get();
        if (leaving.isActive() && firstWaiting().isPresent()) {
            return LeaveResult.removeAndActivateAndNotify(
                    id,
                    leaving,
                    firstWaiting.get(),
                    activationNotification(firstWaiting.get()));
        } else {
            return LeaveResult.justRemove(id, leaving);
        }
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

    public int occupied() {
        return occupied;
    }

    public int nextOrderId() {
        return nextOrderId;
    }

    public Optional<Enrollment> existing() {
        return existing;
    }

    public Optional<Enrollment> firstWaiting() {
        return firstWaiting;
    }
}
