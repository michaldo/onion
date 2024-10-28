package onion4.infrastructure.repository;

import onion4.application.CourseRepository;
import onion4.application.seat.CourseOccupancy;
import onion4.domain.CourseEnrollLeave;
import onion4.domain.CourseNWaiting;
import onion4.domain.CourseUpdate;
import onion4.domain.Enrollment;
import onion4.infrastructure.repository.entities.HibernateCourse;
import onion4.infrastructure.repository.entities.HibernateEnrollment;
import onion4.infrastructure.repository.hibernaterepo.HibernateCourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class CourseRepositoryImpl implements CourseRepository {

    @Autowired private HibernateCourseRepository hibernateRepository;

    @Override
    public List<CourseOccupancy> courseOccupancies() {
        return hibernateRepository.courseOccupancies();
    }

    @Override
    public CourseEnrollLeave courseEnrollLeave(int courseId, int studentId) {
        HibernateCourse hibernateCourse = hibernateRepository.findById(courseId).get();
        Optional<Enrollment> existing = hibernateCourse.enrollments()
                .stream()
                .filter(hibernateEnrollment -> hibernateEnrollment.studentId() == studentId)
                .map(h -> new Enrollment(h.id(), h.studentId(), h.status()))
                .findAny();
        Optional<Enrollment> firstWaiting = hibernateCourse.enrollments()
                .stream()
                .filter(HibernateEnrollment::isWaiting)
                .map(h -> new Enrollment(h.id(), h.studentId(), h.status()))
                .findFirst();
        int nextOrderId = hibernateCourse.enrollments()
                .stream()
                .map(HibernateEnrollment::orderId)
                .max(Comparator.naturalOrder())
                .map(n -> n + 1)
                .orElse(1);

        return new CourseEnrollLeave(
                hibernateCourse.id(),
                hibernateCourse.name(),
                hibernateCourse.limit(),
                hibernateCourse.enrollments().size(),
                existing,
                firstWaiting,
                nextOrderId);
    }


    @Override
    public CourseNWaiting courseNWaiting(int courseId, int nWaiting) {
        HibernateCourse hibernateCourse = hibernateRepository.findById(courseId).get();

        List<Enrollment> firstN = hibernateCourse.enrollments()
                .stream()
                .filter(HibernateEnrollment::isWaiting)
                .limit(nWaiting)
                .map(h -> new Enrollment(h.id(), h.studentId(), h.status()))
                .toList();
        return new CourseNWaiting(
                hibernateCourse.id(),
                hibernateCourse.name(),
                hibernateCourse.limit(),
                firstN);
    }

    @Override
    public void apply(CourseUpdate update) {
        hibernateRepository.findById(update.id())
                .get()
                .apply(update);
    }
}
