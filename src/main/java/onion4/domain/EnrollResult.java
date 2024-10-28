package onion4.domain;

import java.util.Optional;

public record EnrollResult(
        String status,
        Optional<CourseUpdate> courseUpdate
) {
    public EnrollResult(String status) {
        this(status, Optional.empty());
    }

    public EnrollResult(String status, CourseUpdate courseUpdate) {
        this(status, Optional.of(courseUpdate));
    }
}
