package onion3fusion.application;

import onion3fusion.domain.CourseEnrollAggregate;
import onion3fusion.domain.CourseLeaveAggregate;
import onion3fusion.domain.CourseNWaitingAggregate;
import onion3fusion.domain.EnrollResult;
import onion3fusion.domain.LeaveResult;
import onion3fusion.domain.entities.Notification;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.SequencedMap;


@Service
@Transactional
public class CourseService {

    @Autowired CourseRepository courseRepository;

    @Autowired NotificationRepository notificationRepository;

    public String enroll(int courseId, int studentId) {
        CourseEnrollAggregate courseEnroll = courseRepository.courseEnroll(courseId, studentId);
        EnrollResult enrollResult = courseEnroll.enroll(studentId);
        enrollResult.courseUpdate().ifPresent(courseRepository::apply);
        return enrollResult.status();
    }

    public void leave(int courseId, int studentId) {
        CourseLeaveAggregate courseLeave = courseRepository.courseLeave(courseId, studentId);
        LeaveResult leaveResult = courseLeave.leaveExisting();
        leaveResult.courseUpdate().ifPresent(courseRepository::apply);
        leaveResult.notification().ifPresent(notificationRepository::save);
    }

    public void addSeats(SequencedMap<Integer, Integer> seatsAssignment) {
        for (var entry : seatsAssignment.entrySet()) {
            addSeatsToCourse(entry.getKey(), entry.getValue());
        }
    }

    private void addSeatsToCourse(Integer courseId, Integer limitIncrement) {
        CourseNWaitingAggregate courseNWaiting = courseRepository.courseNWaiting(courseId, limitIncrement);
        List<Notification> notifications = courseNWaiting.updateLimitBy(limitIncrement);
        notificationRepository.saveAll(notifications);
    }
}
