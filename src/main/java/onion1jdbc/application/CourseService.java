package onion1jdbc.application;

import onion1jdbc.domain.AddSeatResult;
import onion1jdbc.domain.CourseEnrollLeave;
import onion1jdbc.domain.CourseNWaiting;
import onion1jdbc.domain.EnrollResult;
import onion1jdbc.domain.LeaveResult;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.SequencedMap;


@Service
@Transactional
public class CourseService {

    @Autowired CourseRepository courseRepository;

    @Autowired NotificationRepository notificationRepository;

    public String enroll(int courseId, int studentId) {
        CourseEnrollLeave course = courseRepository.courseEnrollLeave(courseId, studentId);
        EnrollResult enrollResult = course.enroll(studentId);
        enrollResult.courseUpdate().ifPresent(courseRepository::apply);
        return enrollResult.status();
    }

    public void leave(int courseId, int studentId) {
        CourseEnrollLeave course = courseRepository.courseEnrollLeave(courseId, studentId);
        LeaveResult result = course.leaveExisting();
        result.courseUpdate().ifPresent(courseRepository::apply);
        result.notification().ifPresent(notificationRepository::insert);
    }

    public void addSeats(SequencedMap<Integer, Integer> seatsAssignment) {
        for (var entry : seatsAssignment.entrySet()) {
            addSeatsToCourse(entry.getKey(), entry.getValue());
        }
    }

    private void addSeatsToCourse(Integer courseId, Integer limitIncrement) {
        CourseNWaiting course = courseRepository.courseNWaiting(courseId, limitIncrement);
        AddSeatResult addSeatResult = course.updateLimitBy(limitIncrement);
        courseRepository.apply(addSeatResult.courseUpdate());
        notificationRepository.insertAll(addSeatResult.notifications());
    }
}
