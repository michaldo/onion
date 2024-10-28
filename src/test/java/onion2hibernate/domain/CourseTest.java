package onion2hibernate.domain;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CourseTest {

    @Test
    void active_enrollment() {
        // given
        Course course = new Course(1, "dummy", 1, new ArrayList<>());

        // when
        int studentId = 2;
        String status = course.enroll(studentId);

        //
        assertThat(status).isEqualTo("A");
        assertThat(course.enrollments())
                .singleElement()
                .returns(2, Enrollment::studentId)
                .returns(1, Enrollment::orderId)
                .returns("A", Enrollment::status);
    }

    @Test
    void waiting_enrollment() {
        // given
        int dummyId = 1;
        int otherStudentId = 3;
        List<Enrollment> enrollments = List.of(new Enrollment(dummyId, otherStudentId, 1, "A"));
        Course course = new Course(1, "dummy", 1, new ArrayList<>(enrollments));

        // when
        int studentId = 2;
        String status = course.enroll(studentId);

        //
        assertThat(status).isEqualTo("W");
        assertThat(course.enrollments().get(1))
                .returns(2, Enrollment::studentId)
                .returns(2, Enrollment::orderId)
                .returns("W", Enrollment::status);
    }

    @Test
    void leave_enrollment() {
        // given
        int enrollmentId = 1;
        int studentId = 2;
        List<Enrollment> enrollments = List.of(new Enrollment(enrollmentId, studentId, 1, "A"));
        Course course = new Course(1, "dummy", 1, new ArrayList<>(enrollments));

        // when
        course.leave(studentId);

        // then
        assertThat(course.enrollments()).isEmpty();
    }

    @Test
    void waiting_became_active() {
        // given
        List<Enrollment> enrollments = new ArrayList<>(List.of(
                new Enrollment(1, 1, 1, "A"),
                new Enrollment(2, 2, 2, "A"),
                new Enrollment(3, 3, 3, "W")));
        Course course = new Course(100, "dummy", 2, enrollments);

        // when
        Optional<Notification> notification = course.leave(2);

        // then
        assertThat(course.enrollments()).hasSize(2)
                .last()
                .returns(3, Enrollment::orderId)
                .returns("A", Enrollment::status)
                .returns(3, Enrollment::studentId);

        assertThat(notification).get()
                .returns(100, Notification::courseId)
                .returns("A", Notification::status)
                .returns(3, Notification::studentId);

    }

    @Test
    void add_seats_test() {
        // given
        List<Enrollment> enrollments = List.of(
                new Enrollment(1, 1, 1, "A"),
                new Enrollment(2, 2, 2, "W"));
        Course course = new Course(100, "dummy", 1, new ArrayList<>(enrollments));

        // when
        List<Notification> notifications = course.updateLimitBy(2);

        // then
        assertThat(course.limit()).isEqualTo(3);
        assertThat(course.enrollments())
                .last()
                .returns(2, Enrollment::orderId)
                .returns("A", Enrollment::status)
                .returns(2, Enrollment::studentId);
        assertThat(notifications).singleElement()
                .returns(100, Notification::courseId)
                .returns("A", Notification::status)
                .returns(2, Notification::studentId);
    }

}