package onion1jdbc.application;

import onion1jdbc.application.seat.CourseOccupancy;
import onion1jdbc.domain.CourseEnrollLeave;
import onion1jdbc.domain.CourseNWaiting;
import onion1jdbc.domain.CourseUpdate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository {
    List<CourseOccupancy> courseOccupancies();

    CourseEnrollLeave courseEnrollLeave(int courseId, int studentId);

    CourseNWaiting courseNWaiting(int courseId, int nWaiting);

    void apply(CourseUpdate update);
}
