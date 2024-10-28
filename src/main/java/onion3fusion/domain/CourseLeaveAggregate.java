package onion3fusion.domain;

import onion3fusion.domain.entities.CourseBare;
import onion3fusion.domain.entities.Enrollment;
import onion3fusion.domain.entities.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CourseLeaveAggregate {

    private static final Logger logger = LoggerFactory.getLogger(CourseLeaveAggregate.class);

    private final CourseBare course;

    private final Optional<Enrollment> alreadyEnrolled;
    private final Optional<Enrollment> firstWaiting;


    public CourseLeaveAggregate(CourseBare courseBare, Enrollment alreadyEnrolled, Enrollment firstWaiting) {
        this.course = courseBare;
        this.alreadyEnrolled = Optional.ofNullable(alreadyEnrolled);
        this.firstWaiting = Optional.ofNullable(firstWaiting);
    }

    public LeaveResult leaveExisting() {
        if (alreadyEnrolled.isEmpty()) {
            logger.info("Duplication ?");
            return LeaveResult.noop();
        }
        Enrollment leaving = alreadyEnrolled.get();
        if (leaving.isActive() && firstWaiting().isPresent()) {
            Enrollment waiting = firstWaiting.get();
            waiting.activate();
            return LeaveResult.removeAndNotify(leaving, activationNotification(waiting));
        } else {
            return LeaveResult.justRemove(leaving);
        }
    }

    private Notification activationNotification(Enrollment enrollment) {
        return new Notification(course.id(), enrollment.studentId(), "A");
    }

    // for test
    public CourseBare course() {
        return course;
    }

    public Optional<Enrollment> alreadyEnrolled() {
        return alreadyEnrolled;
    }
    public Optional<Enrollment> firstWaiting() {
        return firstWaiting;
    }

}
