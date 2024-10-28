package layer3.logic;

import layer3.data.Course;
import layer3.data.CourseDao;
import layer3.data.Enrollment;
import layer3.data.EnrollmentDao;
import layer3.data.NotificationDao;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.SequencedMap;

@Service
@Transactional
public class CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseService.class);

    @Autowired CourseDao courseDao;

    @Autowired NotificationDao notificationDao;
    @Autowired EnrollmentDao enrollmentDao;


    public String enroll(int courseId, int studentId) {

        Enrollment enrollment =  enrollmentDao.getEnrollment(courseId, studentId);
        if (enrollment != null) {
            logger.info("Duplication");
            return enrollment.status();
        }
        Course course = courseDao.course(courseId);
        int orderId = course.lastOrderId() == null ? 1 : course.lastOrderId() + 1;
        String status = course.activeCount() < course.limit() ? "A" : "W";
        enrollmentDao.enroll(courseId, studentId, orderId, status);
        return status;
    }

    public void leave(int courseId, int studentId) {

        Enrollment enrollment = enrollmentDao.getEnrollment(courseId, studentId);
        if (enrollment == null) {
            logger.info("Duplication");
            return;
        }
        enrollmentDao.remove(enrollment.id());
        if (enrollment.status().equals("A")) {
            List<Enrollment> waiting = enrollmentDao.findFirstNWaiting(courseId,1 );
            if (!waiting.isEmpty()) {
                Enrollment firstWaiting = waiting.getFirst();
                enrollmentDao.activate(firstWaiting.id());
                notificationDao.notify(courseId, firstWaiting.studentId(), "A");
            }
        }
    }

    public void addSeats(SequencedMap<Integer, Integer> seatsAssignment) {
        for (var entry : seatsAssignment.entrySet()) {
            addSeatsToCourse(entry.getKey(), entry.getValue());
        }
    }

    private void addSeatsToCourse(Integer courseId, Integer limitIncrease) {
        Course course = courseDao.course(courseId);
        courseDao.updateLimit(course.id(), course.limit() + limitIncrease);
        List<Enrollment> firstNWaiting = enrollmentDao.findFirstNWaiting(courseId, limitIncrease);
        for (Enrollment waiting : firstNWaiting) {
            enrollmentDao.activate(waiting.id());
            notificationDao.notify(courseId, waiting.studentId(), "A");
        }
    }
}
