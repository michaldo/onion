package onion3fusion.domain;

import onion3fusion.domain.entities.CourseOccupancy;
import onion3fusion.domain.entities.Enrollment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CourseEnrollAggregate {

    private static final Logger logger = LoggerFactory.getLogger(CourseEnrollAggregate.class);

    private final CourseOccupancy courseOccupancy;

    private final Optional<Enrollment> alreadyEnrolled;


    public CourseEnrollAggregate(CourseOccupancy courseOccupancy, Optional<Enrollment> alreadyEnrolled) {
        this.courseOccupancy = courseOccupancy;
        this.alreadyEnrolled = alreadyEnrolled;
    }

    public EnrollResult enroll(int studentId) {

        if (alreadyEnrolled.isPresent()) {
            logger.info("Duplication");
            return new EnrollResult(alreadyEnrolled.get().status());
        }

        String status = courseOccupancy.activeCount() < courseOccupancy.limit() ? "A" : "W";

        Enrollment newEnrollment = new Enrollment(courseOccupancy.id(), studentId, courseOccupancy.nextOrderId(), status);

        logger.info("Student {} enrolled to {} with status {}", studentId, courseOccupancy.id(), status);

        return new EnrollResult(status, newEnrollment);
    }

    public CourseOccupancy occupancy() {
        return courseOccupancy;
    }
}
