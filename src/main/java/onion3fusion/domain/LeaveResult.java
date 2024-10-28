package onion3fusion.domain;

import onion3fusion.domain.entities.Enrollment;
import onion3fusion.domain.entities.Notification;

import java.util.Optional;

public record LeaveResult(
        Optional<CourseUpdate> courseUpdate,
        Optional<Notification> notification) {

    public static LeaveResult noop() {
        return new LeaveResult(Optional.empty(), Optional.empty());
    }

    public static LeaveResult justRemove(Enrollment leaving) {
        return new LeaveResult(CourseUpdate.justRemove(leaving), Optional.empty());
    }

    public static LeaveResult removeAndNotify(Enrollment leaving, Notification notification) {
        return new LeaveResult(CourseUpdate.justRemove(leaving), Optional.of(notification));
    }
}
