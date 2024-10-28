package onion3fusion.domain;

import onion3fusion.domain.entities.Enrollment;

import java.util.Optional;

public record EnrollResult(
        String status,
        Optional<CourseUpdate> courseUpdate
) {
    public EnrollResult(String status) {
        this(status, Optional.empty());
    }

    public EnrollResult(String status, Enrollment enrollment) {
        this(status, Optional.of(CourseUpdate.justAdd(enrollment)));
    }
}
