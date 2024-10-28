package onion2hibernate.application;

import onion2hibernate.application.seat.CourseOccupancy;
import onion2hibernate.domain.Course;
import onion2hibernate.domain.Enrollment;
import onion2hibernate.domain.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig
@ComponentScan
@Import({})
class ApplicationTest {


    @MockitoBean CourseRepository courseRepository;

    @MockitoBean NotificationRepository notificationRepository;

    @Autowired CourseService courseService;

    @Autowired SeatService seatService;

    @Test
    void test_enroll() {
        // given
        int limit = 0;
        when(courseRepository.findById(100)).thenReturn(blankCourse(limit));

        // when
        String status = courseService.enroll(100, 1);

        // then
        assertThat(status).isEqualTo("W");

    }

    @Test
    void test_leave() {
        // given
        int limit = 1;
        Enrollment active = new Enrollment(1, 1, 1, "A");
        Enrollment waiting = new Enrollment(2, 2, 2, "W");
        when(courseRepository.findById(100)).thenReturn(occupied(limit, List.of(active, waiting)));

        // when
        courseService.leave(100, 1);

        // then
        verify(notificationRepository).save(argThat(n ->
            n.status().equals("A") && n.courseId() == 100 && n.studentId() == 2));
    }

    @Test
    void test_add_seats() {
        // given
        int limit = 1;
        Enrollment active = new Enrollment(1, 1, 1, "A");
        Enrollment waiting = new Enrollment(2, 2, 2, "W");
        Optional<Course> course = occupied(limit, List.of(active, waiting));
        when(courseRepository.findById(100)).thenReturn(course);
        when(courseRepository.courseOccupancies()).thenReturn(List.of(
                new CourseOccupancy(100, "dummy", 1, 1L, 1L)));

        // when
        seatService.addSeats(2);

        // then
        assertThat(course).get().extracting(Course::limit).isEqualTo(3);
        verify(notificationRepository).saveAll(argThat(it ->
                        assertThat(it.iterator().next())
                                .returns(100, Notification::courseId)
                                .returns(2, Notification::studentId)
                                .returns("A", Notification::status) != null));

    }

    private Optional<Course> blankCourse(int limit) {
        return Optional.of(new Course(
                100,
                "dummy",
                limit,
                new ArrayList<>()));
    }

    private Optional<Course> occupied(int limit, List<Enrollment> enrollments) {
        return Optional.of(new Course(
                100,
                "dummy",
                limit,
                new ArrayList<>(enrollments)));
    }
}
