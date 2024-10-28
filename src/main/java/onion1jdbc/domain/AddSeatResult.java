package onion1jdbc.domain;

import java.util.List;

public record AddSeatResult(
        CourseUpdate courseUpdate,
        List<Notification> notifications
) {
}
