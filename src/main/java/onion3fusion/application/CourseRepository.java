package onion3fusion.application;

import onion3fusion.domain.CourseEnrollAggregate;
import onion3fusion.domain.CourseLeaveAggregate;
import onion3fusion.domain.CourseNWaitingAggregate;
import onion3fusion.domain.CourseUpdate;
import onion3fusion.domain.entities.CourseOccupancy;

import java.util.List;

public interface CourseRepository {


    List<CourseOccupancy> courseOccupancies();

    CourseNWaitingAggregate courseNWaiting(int courseId, int nWaiting);


    CourseEnrollAggregate courseEnroll(int courseId, int studentId);

    CourseLeaveAggregate courseLeave(int courseId, int studentId);

    void apply(CourseUpdate courseUpdate);
}
