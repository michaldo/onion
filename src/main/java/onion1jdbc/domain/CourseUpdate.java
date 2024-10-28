package onion1jdbc.domain;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public record CourseUpdate(
        int id,
        OptionalInt newLimit,
        Optional<NewEnrollment> toBeAdded,
        Optional<Enrollment> toBeRemoved,
        List<Enrollment> toBeActivated
) {

    public static CourseUpdate justAdd(int courseId, NewEnrollment toBeAdded) {
        return new CourseUpdate(courseId, OptionalInt.empty(), Optional.of(toBeAdded), Optional.empty(), List.of());
    }

    public static Optional<CourseUpdate> justRemove(int courseId, Enrollment toBeRemoved) {
        return Optional.of(
                new CourseUpdate(
                        courseId, OptionalInt.empty(), Optional.empty(), Optional.of(toBeRemoved), List.of()));
    }

    public record NewEnrollment(
            int studentId,
            int orderId,
            String status
    ) {}
}
