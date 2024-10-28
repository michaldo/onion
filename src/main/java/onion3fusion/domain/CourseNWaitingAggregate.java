package onion3fusion.domain;

import onion3fusion.domain.entities.CourseBare;
import onion3fusion.domain.entities.Enrollment;
import onion3fusion.domain.entities.Notification;

import java.util.List;

public class CourseNWaitingAggregate {

    private final CourseBare course;

    private final List<Enrollment> firstWaiting;

    public CourseNWaitingAggregate(CourseBare course, List<Enrollment> firstWaiting) {
        this.course = course;
        this.firstWaiting = firstWaiting;
    }

    public List<Notification> updateLimitBy(Integer n) {
        course.updateLimitBy(n);
        return firstWaiting.stream()
                .peek(Enrollment::activate)
                .map(this::activationNotification)
                .toList();
    }

    private Notification activationNotification(Enrollment enrollment) {
        return new Notification(course.id(), enrollment.studentId(), "A");
    }

    // for test
    public int id() {
        return course.id();
    }

    public List<Enrollment> firstWaiting() {
        return firstWaiting;
    }

}
