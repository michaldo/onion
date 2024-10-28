package onion3fusion.infrastructure.repository;

import onion3fusion.domain.CourseEnrollAggregate;
import onion3fusion.domain.CourseLeaveAggregate;
import onion3fusion.domain.CourseUpdate;
import onion3fusion.domain.entities.Enrollment;

import onion3fusion.application.CourseRepository;
import onion3fusion.domain.entities.CourseOccupancy;
import onion3fusion.domain.entities.CourseBare;
import onion3fusion.domain.CourseNWaitingAggregate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class CourseRepositoryImpl implements CourseRepository {

    @Autowired CourseBareRepository courseBareRepository;
    @Autowired EnrollementRepository enrollementRepository;

    @Override
    public List<CourseOccupancy> courseOccupancies() {
        return courseBareRepository.courseOccupancies();
    }

    @Override
    public CourseEnrollAggregate courseEnroll(int courseId, int studentId) {
        CourseOccupancy courseOccupancy = courseBareRepository.courseOccupancy(courseId);
        Optional<Enrollment> alreadyEnrolled = enrollementRepository.findByCourseIdAndStudentId(courseId, studentId);
        return new CourseEnrollAggregate(courseOccupancy, alreadyEnrolled);
    }

    @Override
    public CourseLeaveAggregate courseLeave(int courseId, int studentId) {
        return courseBareRepository.courseLeaveAggregate(courseId, studentId);
    }

    @Override
    public CourseNWaitingAggregate courseNWaiting(int courseId, int n) {
        CourseBare courseBare = courseBareRepository.findById(courseId).get();
        List<Enrollment> nWaiting = enrollementRepository.firstNWaiting(courseId, n);

        return new CourseNWaitingAggregate(courseBare, nWaiting);
    }

    @Override
    public void apply(CourseUpdate courseUpdate) {
        courseUpdate.toBeAdded().ifPresent(enrollementRepository::save);
        courseUpdate.toBeRemoved().ifPresent(enrollementRepository::delete);
    }
}
