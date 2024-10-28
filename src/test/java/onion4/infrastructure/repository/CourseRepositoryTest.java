package onion4.infrastructure.repository;

import onion4.application.seat.CourseOccupancy;
import onion4.domain.CourseEnrollLeave;
import onion4.domain.CourseNWaiting;
import onion4.domain.CourseUpdate;
import onion4.domain.CourseUpdate.NewEnrollment;
import onion4.domain.Enrollment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(CourseRepositoryImpl.class)
class CourseRepositoryTest {

    @Autowired CourseRepositoryImpl repository;

    @Autowired JdbcTemplate jdbcTemplate;

    @Autowired TestEntityManager testEntityManager;

    @BeforeEach
    void cleanupEnrollment() {
        JdbcTestUtils.deleteFromTables(jdbcTemplate, "course", "enrollment");
    }

    @Test
    void test_occupancies_empty() {
        // given
        jdbcTemplate.update("INSERT INTO course VALUES(100, 'dummy', 2)");

        // when
        List<CourseOccupancy> occupancies = repository.courseOccupancies();

        // then
        assertThat(occupancies)
                .singleElement()
                .isEqualTo(new CourseOccupancy(100, "dummy", 2, 0,0));

    }

    @Test
    void test_occupancies() {
        // given
        jdbcTemplate.update("INSERT INTO course VALUES(100, 'dummy', 2)");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(1, 100, 1, 1, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(2, 100, 2, 2, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(3, 100, 3, 3, 'W')");

        // when
        List<CourseOccupancy> occupancies = repository.courseOccupancies();

        // then
        assertThat(occupancies)
                .singleElement()
                .isEqualTo(new CourseOccupancy(100, "dummy", 2, 2,1));

    }

    @Test
    void test_courseEnrollLeave() {
        // given
        jdbcTemplate.update("INSERT INTO course VALUES(100, 'foo', 2)");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(1, 100, 1, 1, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(2, 100, 2, 2, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(3, 100, 3, 3, 'W')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(4, 100, 4, 4, 'W')");

        // when
        CourseEnrollLeave course = repository.courseEnrollLeave(100, 1);

        // then
        assertThat(course)
                .returns(100, CourseEnrollLeave::id)
                .returns("foo", CourseEnrollLeave::name)
                .returns(2, CourseEnrollLeave::limit)
                .returns(4, CourseEnrollLeave::occupied)
                .returns(5, CourseEnrollLeave::nextOrderId)
                .returns(new Enrollment(1,1, "A"), c -> c.existing().get())
                .returns(new Enrollment(3,3, "W"), c -> c.firstWaiting().get());
    }

    @Test
    void test_courseEnrollLeave_empty() {
        // given
        jdbcTemplate.update("INSERT INTO course VALUES(100, 'foo', 2)");

        // when
        CourseEnrollLeave course = repository.courseEnrollLeave(100, 1);

        // then
        assertThat(course)
                .returns(100, CourseEnrollLeave::id)
                .returns("foo", CourseEnrollLeave::name)
                .returns(2, CourseEnrollLeave::limit)
                .returns(0, CourseEnrollLeave::occupied)
                .returns(1, CourseEnrollLeave::nextOrderId)
                .returns(Optional.empty(), CourseEnrollLeave::existing)
                .returns(Optional.empty(), CourseEnrollLeave::firstWaiting);
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
        CourseNWaiting course = repository.courseNWaiting(100, 2);

        // then
        assertThat(course)
                .returns(100, CourseNWaiting::id)
                .returns("foo", CourseNWaiting::name)
                .returns(2, CourseNWaiting::limit)
                .returns(
                        List.of(
                                new Enrollment(3,3, "W"),
                                new Enrollment(4,4, "W")),
                        CourseNWaiting::firstNWaiting);
    }

    @Test
    void test_courseNWaiting_empty() {
        // given
        jdbcTemplate.update("INSERT INTO course VALUES(100, 'foo', 2)");

        // when
        CourseNWaiting course = repository.courseNWaiting(100, 2);

        // then
        assertThat(course)
                .returns(100, CourseNWaiting::id)
                .returns(emptyList(), CourseNWaiting::firstNWaiting);
    }

    @Test
    void test_update() {
        // given
        jdbcTemplate.update("INSERT INTO course VALUES(100, 'dummy', 2)");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(1, 100, 1, 1, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(2, 100, 2, 2, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(3, 100, 3, 3, 'W')");

        var newLimit = OptionalInt.of(4);
        var toBeAdded = Optional.of(new NewEnrollment(4,4, "A"));
        var toBeRemoved = Optional.of(new Enrollment(1,1, "A"));
        var toBeActivated = List.of(new Enrollment(3,3, "A"));

        // when
        repository.apply(new CourseUpdate(
                100,
                newLimit,
                toBeAdded,
                toBeRemoved,
                toBeActivated));

        // then
        testEntityManager.flush();
        int courseUpdated = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "course",
                "id = 100 AND course.limit = 4");
        assertThat(courseUpdated).isEqualTo(1);

        int added = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "enrollment",
                "course_id = 100 AND student_id = 4 AND order_id = 4 AND status = 'A'");
        assertThat(added).isEqualTo(1);

        int removed = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "enrollment",
                "id = 1");
        assertThat(removed).isEqualTo(0);

        int activated = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "enrollment",
                "course_id = 100 AND student_id IN (3,4) AND status = 'A'");
        assertThat(activated).isEqualTo(2);
    }
}
