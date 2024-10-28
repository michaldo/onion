package onion4.domain;

import java.util.List;

public record AddSeatResult(
        CourseUpdate courseUpdate,
        List<Notification> notifications
) {
}
