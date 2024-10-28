package onion4;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Sql(scripts = "/data.sql")
class Onion4ApplicationTest {

    @Autowired JdbcTemplate jdbcTemplate;

    @Test
    void active_enrollment(@Autowired TestRestTemplate restTemplate) {
        String A = restTemplate.patchForObject(
                "/enroll?course-id={courseId}&student-id={studentId}",
                null,
                String.class,
                100, 2);
        assertThat(A).isEqualTo("A");
    }

    @Test
    void waiting_enrollment(@Autowired TestRestTemplate restTemplate) {
        // given
        restTemplate.patchForObject(
                "/enroll?course-id={courseId}&student-id={studentId}",
                null,
                String.class,
                100, 2);
        // when
        String W = restTemplate.patchForObject(
                "/enroll?course-id={courseId}&student-id={studentId}",
                null,
                String.class,
                100, 3);
        // then
        assertThat(W).isEqualTo("W");
    }

    @Test
    void leave_enrollment(@Autowired TestRestTemplate restTemplate) {
        // when
        restTemplate.patchForObject(
                "/leave?course-id={courseId}&student-id={studentId}",
                null,
                String.class,
                100, 1);
        // then
        int count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*) FROM enrollment 
                WHERE course_id = ? and student_id = ?""",
                Integer.class, 100, 1);
        // then
        assertThat(count).isZero();
    }

    @Test
    void waiting_became_active(@Autowired TestRestTemplate restTemplate) {

        // when
        restTemplate.patchForObject(
                "/enroll?course-id={courseId}&student-id={studentId}",
                null,
                String.class,
                100, 2);
        restTemplate.patchForObject(
                "/enroll?course-id={courseId}&student-id={studentId}",
                null,
                String.class,
                100, 3);

        // when
        restTemplate.patchForObject(
                "/leave?course-id={courseId}&student-id={studentId}",
                null,
                String.class,
                100, 1);
        // then
        String A = jdbcTemplate.queryForObject("""
                SELECT status FROM enrollment 
                WHERE course_id = ? and student_id = ?""",
                String.class, 100, 3);
        // then
        assertThat(A).isEqualTo("A");
    }

    @Test
    void add_seats_test(@Autowired TestRestTemplate restTemplate) {

        // when
        restTemplate.patchForObject(
                "/enroll?course-id={courseId}&student-id={studentId}",
                null,
                String.class,
                100, 2);
        restTemplate.patchForObject(
                "/enroll?course-id={courseId}&student-id={studentId}",
                null,
                String.class,
                100, 3);
        restTemplate.patchForObject(
                "/enroll?course-id={courseId}&student-id={studentId}",
                null,
                String.class,
                100, 4);

        restTemplate.patchForObject(
                "/enroll?course-id={courseId}&student-id={studentId}",
                null,
                String.class,
                101, 1);

        restTemplate.patchForObject(
                "/enroll?course-id={courseId}&student-id={studentId}",
                null,
                String.class,
                101, 2);

        // when
        restTemplate.postForEntity(
                "/seat?seats={seats}",
                null,
                Void.class,
                4);
        // then

        int limitMath = jdbcTemplate.queryForObject("SELECT course.limit FROM course WHERE id = 100", Integer.class);
        assertThat(limitMath).isEqualTo(5);
        int limitPhysics = jdbcTemplate.queryForObject("SELECT course.limit FROM course WHERE id = 101", Integer.class);
        assertThat(limitPhysics).isEqualTo(2);

        int i = jdbcTemplate.queryForObject("SELECT 1 FROM notification WHERE course_id = 100 and student_id = 4", Integer.class);
        assertThat(i).isEqualTo(1);
    }

}
