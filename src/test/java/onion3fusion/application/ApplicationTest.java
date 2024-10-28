package onion3fusion.application;

import onion3fusion.domain.CourseEnrollAggregate;
import onion3fusion.domain.CourseLeaveAggregate;
import onion3fusion.domain.CourseNWaitingAggregate;
import onion3fusion.domain.entities.CourseBare;
import onion3fusion.domain.entities.CourseOccupancy;
import onion3fusion.domain.entities.Enrollment;
import onion3fusion.domain.entities.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
        when(courseRepository.courseEnroll(100, 1)).thenReturn(blankCourse(limit));

        // when
        String status = courseService.enroll(100, 1);

        // then
        assertThat(status).isEqualTo("W");
        verify(courseRepository).apply(any());

    }

    @Test
    void test_leave() {
        // given
        int limit = 1;
        Enrollment active = new Enrollment(1, 1, 1, "A");
        Enrollment waiting = new Enrollment(2, 2, 2, "W");
        when(courseRepository.courseLeave(100,1)).thenReturn(occupied(limit,active, waiting));

        // when
        courseService.leave(100, 1);

        // then
        verify(courseRepository).apply(argThat(update -> update.toBeRemoved().get() == active));
        verify(notificationRepository).save(argThat(n ->
            n.status().equals("A") && n.courseId() == 100 && n.studentId() == 2));
    }

    @Test
    void test_add_seats() {
        // given
        int limit = 1;
        Enrollment waiting = new Enrollment(2, 2, 2, "W");
        CourseBare course = new CourseBare(100, "dummy", limit);
        CourseNWaitingAggregate courseNWaiting =  new CourseNWaitingAggregate(course, List.of(waiting));

        when(courseRepository.courseNWaiting(100, 2)).thenReturn(courseNWaiting);
        when(courseRepository.courseOccupancies()).thenReturn(List.of(
                new CourseOccupancy(100, "dummy", 1, 1L, 1L, 0)));

        // when
        seatService.addSeats(2);

        // then
        assertThat(course.limit()).isEqualTo(3);
        assertThat(waiting.status()).isEqualTo("A");
        verify(notificationRepository).saveAll(argThat(it ->
                        assertThat(it.iterator().next())
                                .returns(100, Notification::courseId)
                                .returns(2, Notification::studentId)
                                .returns("A", Notification::status) != null));

    }

    private CourseEnrollAggregate blankCourse(int limit) {
        return new CourseEnrollAggregate(
                new CourseOccupancy(100, "dummy", limit,0L,0L,1),
                Optional.empty());
    }

    private CourseLeaveAggregate occupied(int limit, Enrollment active, Enrollment waiting) {
        return new CourseLeaveAggregate(
                new CourseBare(100, "dummy", limit),
                active,
                waiting);
    }
}
