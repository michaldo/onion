package onion3fusion.domain;

import onion3fusion.domain.entities.CourseBare;
import onion3fusion.domain.entities.CourseOccupancy;
import onion3fusion.domain.entities.Enrollment;
import onion3fusion.domain.entities.Notification;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CourseTest {

    @Test
    void active_enrollment() {
        // given
        CourseEnrollAggregate course = new CourseEnrollAggregate(
                new CourseOccupancy(100, "dummy", 1, 0L,0L,1),
                Optional.empty());

        // when
        int studentId = 2;
        EnrollResult result = course.enroll(studentId);

        //
        assertThat(result.status()).isEqualTo("A");
        assertThat(result.courseUpdate().get().toBeAdded().get())
                .returns(2, Enrollment::studentId)
                .returns(1, Enrollment::orderId)
                .returns("A", Enrollment::status);
    }

    @Test
    void waiting_enrollment() {
        // given
        CourseEnrollAggregate course = new CourseEnrollAggregate(
                new CourseOccupancy(100, "dummy", 1, 1L,0L,2),
                Optional.empty());

        // when
        int studentId = 2;
        EnrollResult result = course.enroll(studentId);;

        // then
        assertThat(result.status()).isEqualTo("W");
        assertThat(result.courseUpdate().get().toBeAdded().get())
                .returns(2, Enrollment::studentId)
                .returns(2, Enrollment::orderId)
                .returns("W", Enrollment::status);
    }

    @Test
    void leave_enrollment() {
        // given
        Enrollment existing = new Enrollment(100, 2, 1, "A");
        CourseLeaveAggregate course = new CourseLeaveAggregate(
                new CourseBare(100, "dummy", 1), existing, null);

        // when
        LeaveResult result = course.leaveExisting();

        // then
        assertThat(result.courseUpdate().get().toBeRemoved().get()).isEqualTo(existing);
    }

    @Test
    void waiting_became_active() {
        // given
        Enrollment existingActive = new Enrollment(100, 2, 1, "A");
        Enrollment firstWaiting = new Enrollment(100, 3, 2, "W");
        CourseLeaveAggregate course = new CourseLeaveAggregate(
                new CourseBare(100, "dummy", 1),
                existingActive,
                firstWaiting);

        // when
        LeaveResult result = course.leaveExisting();

        // then
        assertThat(result.courseUpdate().get().toBeRemoved().get()).isEqualTo(existingActive);
        assertThat(firstWaiting.status()).isEqualTo("A");
        assertThat(result.notification()).get()
                .returns(100, Notification::courseId)
                .returns("A", Notification::status)
                .returns(3, Notification::studentId);
    }

    @Test
    void add_seats_test() {
        // given
        int limit = 1;
        List<Enrollment> enrollments = List.of(
                new Enrollment(2, 2, 2, "W"));
        CourseBare courseBare = new CourseBare(100, "dummy", limit);
        CourseNWaitingAggregate course = new CourseNWaitingAggregate(
                courseBare,
                enrollments);

        // when
        List<Notification> notifications = course.updateLimitBy(2);

        //
        assertThat(courseBare.limit()).isEqualTo(3);
        assertThat(notifications).singleElement()
                .returns(100, Notification::courseId)
                .returns("A", Notification::status)
                .returns(2, Notification::studentId);
        assertThat(enrollments)
                .last()
                .returns(2, Enrollment::orderId)
                .returns("A", Enrollment::status)
                .returns(2, Enrollment::studentId);
    }

}