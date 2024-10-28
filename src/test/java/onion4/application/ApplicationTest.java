package onion4.application;

import onion4.application.seat.CourseOccupancy;
import onion4.domain.CourseNWaiting;

import onion4.domain.CourseEnrollLeave;
import onion4.domain.CourseUpdate.NewEnrollment;
import onion4.domain.Enrollment;
import onion4.domain.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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
        when(courseRepository.courseEnrollLeave(100, 1)).thenReturn(blankCourse(limit));

        // when
        String status = courseService.enroll(100, 1);

        // then
        assertThat(status).isEqualTo("W");
        verify(courseRepository).apply(argThat(update ->
                update.toBeAdded().get().equals(
                        new NewEnrollment(1, 1, "W"))));
    }

    @Test
    void test_leave() {
        // given
        int limit = 1;
        Enrollment active = new Enrollment(1, 1, "A");
        Enrollment waiting = new Enrollment(2, 2, "W");
        when(courseRepository.courseEnrollLeave(100, 1)).thenReturn(activeWaiting(limit, active, waiting));

        // when
        courseService.leave(100, 1);

        // then
        verify(courseRepository).apply(argThat(update ->
                update.toBeRemoved().get().equals(active)));
        verify(notificationRepository).insert(new Notification(100, 2, "A"));
    }

    @Test
    void test_add_seats() {
        // given
        int limit = 1;
        Enrollment waiting = new Enrollment(2, 2, "W");
        when(courseRepository.courseNWaiting(100, 2)).thenReturn(nWaiting(limit,waiting));
        when(courseRepository.courseOccupancies()).thenReturn(List.of(
                new CourseOccupancy(100, "dummy", 1, 1, 1)));

        // when
        seatService.addSeats(2);

        // then
        verify(courseRepository).apply(argThat(update ->
                update.newLimit().getAsInt() == 3 &&
                update.toBeActivated().getFirst().equals(waiting)));
        verify(notificationRepository).insertAll(argThat(list ->
                list.getFirst().equals(new Notification(100, 2, "A"))));
    }

    private CourseEnrollLeave blankCourse(int limit) {
        return new CourseEnrollLeave(
                100,
                "dummy",
                limit,
                0,
                Optional.empty(),
                Optional.empty(),
                1);
    }

    private CourseEnrollLeave activeWaiting(int limit, Enrollment active, Enrollment waiting) {
        return new CourseEnrollLeave(
                100,
                "dummy",
                limit,
                2,
                Optional.of(active),
                Optional.of(waiting),
                3);
    }

    private CourseNWaiting nWaiting(int limit, Enrollment waiting) {
        return new CourseNWaiting(
                100,
                "dummy",
                limit,
                List.of(waiting));
    }
}
