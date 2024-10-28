package onion4.domain;


import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public record LeaveResult(
        Optional<CourseUpdate> courseUpdate,
        Optional<Notification> notification) {

    public static LeaveResult noop() {
        return new LeaveResult(Optional.empty(), Optional.empty());
    }

    public static LeaveResult justRemove(int courseId, Enrollment leaving) {
        return new LeaveResult(CourseUpdate.justRemove(courseId,leaving), Optional.empty());
    }

    public static LeaveResult removeAndActivateAndNotify(int courseId, Enrollment leaving, Enrollment toActivate, Notification notification) {
        return new LeaveResult(
                Optional.of(new CourseUpdate(
                        courseId,
                        OptionalInt.empty(),
                        Optional.empty(),
                        Optional.of(leaving),
                        List.of(toActivate))),
                Optional.of(notification));
    }
}

