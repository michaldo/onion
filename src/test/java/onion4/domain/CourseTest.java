package onion4.domain;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static onion4.domain.CourseUpdate.NewEnrollment;

class CourseTest {

    @ParameterizedTest
    @CsvSource(textBlock = """
            # limit, occupied, expected status
            10     ,        9, A
            10     ,       10, W
            """)
    void active_enrollment(int limit, int occupied, String expectedStatus) {
        // given
        CourseEnrollLeave course = new CourseEnrollLeave(
                100, "dummy", limit, occupied, Optional.empty(), Optional.empty(),
                13);

        // when
        int studentId = 2;
        EnrollResult result = course.enroll(studentId);

        //
        assertThat(result.status()).isEqualTo(expectedStatus);
        assertThat(result.courseUpdate().get().toBeAdded().get())
                .returns(2, CourseUpdate.NewEnrollment::studentId)
                .returns(expectedStatus, NewEnrollment::status)
                .returns(13, NewEnrollment::orderId);
    }


    @Test
    void leave_enrollment() {
        // given
        int enrollmentId = 1;
        int studentId = 2;
        int dummy = 7;
        CourseEnrollLeave course = new CourseEnrollLeave(
                dummy, "dummy", dummy, dummy, Optional.of(new Enrollment(enrollmentId, studentId, "A")), Optional.empty(),
                dummy);

        // when
        LeaveResult result = course.leaveExisting();

        //
        assertThat(result.courseUpdate().get().toBeRemoved()).get()
                .returns(enrollmentId, Enrollment::id);
    }

    @Test
    void waiting_became_active() {
        // given
        Optional<Enrollment> existingEnrollment = Optional.of(
                new Enrollment(1, 1, "A"));

        Optional<Enrollment> firstWaiting = Optional.of(
                new Enrollment(3, 3, "W"));

        int dummy = 7;
        CourseEnrollLeave course = new CourseEnrollLeave(
                dummy, "dummy", dummy, dummy, existingEnrollment, firstWaiting,
                dummy);

        // when
        LeaveResult result = course.leaveExisting();

        //
        assertThat(result.courseUpdate().get().toBeActivated())
                .singleElement()
                .returns(3, Enrollment::id);
    }

    @Test
    void add_seats_test() {
        // given
        List<Enrollment> firstNWaiting = List.of(
                new Enrollment(2, 2, "W"));
        CourseNWaiting course = new CourseNWaiting(100, "dummy", 1, firstNWaiting);

        // when
        AddSeatResult result = course.updateLimitBy(2);

        //
        assertThat(result.courseUpdate().newLimit().getAsInt()).isEqualTo(3);
        assertThat(result.courseUpdate().toBeActivated())
                .singleElement()
                .returns(2, Enrollment::id);
        assertThat(result.notifications())
        .singleElement()
                .returns(100, Notification::courseId)
                .returns(2, Notification::studentId)
                .returns("A", Notification::status);
    }

}