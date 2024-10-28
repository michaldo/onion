package onion1jdbc.domain;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class CourseNWaiting {

    private final int id;
    private final String name;
    private final int limit;
    private final List<Enrollment> firstNWaiting;

    public CourseNWaiting(int id, String name, int limit, List<Enrollment> firstNWaiting) {
        this.id = id;
        this.name = name;
        this.limit = limit;
        this.firstNWaiting = firstNWaiting;
    }

    public AddSeatResult updateLimitBy(Integer n) {
        List<Notification> notifications = firstNWaiting
                .stream()
                .map(this::activationNotification)
                .toList();


        return new AddSeatResult(
                new CourseUpdate(
                    id,
                    OptionalInt.of(limit + n),
                        Optional.empty(),
                        Optional.empty(),
                        firstNWaiting),
                notifications);
    }


    private Notification activationNotification(Enrollment enrollment) {
        return new Notification(id, enrollment.studentId(), "A");
    }

    // for tests (now)
    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public int limit() {
        return limit;
    }

    public List<Enrollment> firstNWaiting() {
        return firstNWaiting;
    }
}
