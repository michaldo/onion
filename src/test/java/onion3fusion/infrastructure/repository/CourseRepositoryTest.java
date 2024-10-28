package onion3fusion.infrastructure.repository;



import onion3fusion.domain.CourseEnrollAggregate;
import onion3fusion.domain.CourseLeaveAggregate;
import onion3fusion.domain.CourseNWaitingAggregate;
import onion3fusion.domain.entities.CourseBare;
import onion3fusion.domain.entities.CourseOccupancy;
import onion3fusion.domain.entities.Enrollment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.list;

@DataJpaTest
@Import(CourseRepositoryImpl.class)
class CourseRepositoryTest {

    @Autowired CourseRepositoryImpl repository;

    @Autowired JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanupEnrollment() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "course", "enrollment");
    }


    @Test
    void test_course_enroll() {
        // given
        jdbcTemplate.update("INSERT INTO course VALUES(100, 'foo', 2)");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(1, 100, 1, 1, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(2, 100, 2, 2, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(3, 100, 3, 3, 'W')");

        // when
        CourseEnrollAggregate courseEnrollLeave = repository.courseEnroll(100,4);

        // then
        assertThat(courseEnrollLeave.occupancy())
                .returns(100, CourseOccupancy::id)
                .returns("foo", CourseOccupancy::name)
                .returns(2, CourseOccupancy::limit)
                .returns(2L, CourseOccupancy::activeCount)
                .returns(1L, CourseOccupancy::waitingCount)
                .returns(4, CourseOccupancy::nextOrderId);
    }

    @Test
    void test_course_leave() {
        // given
        jdbcTemplate.update("INSERT INTO course VALUES(100, 'foo', 2)");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(1, 100, 1, 1, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(2, 100, 2, 2, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(3, 100, 3, 3, 'W')");

        // when
        CourseLeaveAggregate courseLeave = repository.courseLeave(100,2);

        // then
        assertThat(courseLeave.course())
                .returns(100, CourseBare::id);
        assertThat(courseLeave.alreadyEnrolled()).get()
                .returns(2, Enrollment::id);
        assertThat(courseLeave.firstWaiting()).get()
                .returns(3, Enrollment::id);

    }

    @Test
    void test_courseNWaiting() {
        // given
        jdbcTemplate.update("INSERT INTO course VALUES(100, 'foo', 2)");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(1, 100, 1, 1, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(2, 100, 2, 2, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(3, 100, 3, 3, 'W')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(4, 100, 4, 4, 'W')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(5, 100, 5, 5, 'W')");

        // when
        CourseNWaitingAggregate courseNWaiting = repository.courseNWaiting(100, 2);

        // then
        assertThat(courseNWaiting)
                .returns(100, CourseNWaitingAggregate::id)
                .extracting(CourseNWaitingAggregate::firstWaiting, list(Enrollment.class))
                .extracting(Enrollment::id)
                .containsOnly(3,4);
    }

}
