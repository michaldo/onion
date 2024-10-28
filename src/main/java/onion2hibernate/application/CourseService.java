package onion2hibernate.application;

import jakarta.transaction.Transactional;
import onion2hibernate.domain.Course;
import onion2hibernate.domain.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.SequencedMap;


@Service
@Transactional
public class CourseService {

    @Autowired CourseRepository courseRepository;

    @Autowired NotificationRepository notificationRepository;

    public String enroll(int courseId, int studentId) {
        Course course = courseRepository.findById(courseId).get();
        String status = course.enroll(studentId);
        return status;
    }

    public void leave(int courseId, int studentId) {
        Course course = courseRepository.findById(courseId).get();
        Optional<Notification> notifyWaiting = course.leave(studentId);
        notifyWaiting.ifPresent(notificationRepository::save);
    }

    public void addSeats(SequencedMap<Integer, Integer> seatsAssignment) {
        for (var entry : seatsAssignment.entrySet()) {
            addSeatsToCourse(entry.getKey(), entry.getValue());
        }
    }

    private void addSeatsToCourse(Integer courseId, Integer limitIncrement) {
        courseRepository.findById(courseId)
                .map(course -> course.updateLimitBy(limitIncrement))
                .ifPresent(notificationRepository::saveAll);
    }
}
