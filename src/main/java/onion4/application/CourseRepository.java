package onion4.application;

import onion4.application.seat.CourseOccupancy;
import onion4.domain.CourseEnrollLeave;
import onion4.domain.CourseNWaiting;
import onion4.domain.CourseUpdate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository {
    List<CourseOccupancy> courseOccupancies();

    CourseEnrollLeave courseEnrollLeave(int courseId, int studentId);

    CourseNWaiting courseNWaiting(int courseId, int nWaiting);

    void apply(CourseUpdate update);
}
