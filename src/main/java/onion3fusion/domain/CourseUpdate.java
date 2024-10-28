package onion3fusion.domain;

import onion3fusion.domain.entities.Enrollment;

import java.util.Optional;

public record CourseUpdate(
        Optional<Enrollment> toBeAdded,
        Optional<Enrollment> toBeRemoved
) {

    public static CourseUpdate justAdd(Enrollment toBeAdded) {
        return new CourseUpdate(Optional.of(toBeAdded), Optional.empty());
    }

    public static Optional<CourseUpdate> justRemove(Enrollment toBeRemoved) {
        return Optional.of(
                new CourseUpdate(
                        Optional.empty(),
                        Optional.of(toBeRemoved)));
    }

}
