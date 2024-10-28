package onion2hibernate.infrastructure.repository;


import onion2hibernate.application.CourseRepository;
import onion2hibernate.application.seat.CourseOccupancy;
import onion2hibernate.domain.Course;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;

@DataJpaTest
class CourseRepositoryTest {

    @Autowired CourseRepository repository;

    @Autowired JdbcTemplate jdbcTemplate;

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
                .isEqualTo(new CourseOccupancy(100, "dummy", 2, 0L,0L));

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
                .isEqualTo(new CourseOccupancy(100, "dummy", 2, 2L,1L));
    }



    @Test
    void test_course() {
        // given
        jdbcTemplate.update("INSERT INTO course VALUES(100, 'foo', 2)");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(1, 100, 1, 1, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(2, 100, 2, 2, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(3, 100, 3, 3, 'W')");

        // when
        Optional<Course> course = repository.findById(100);

        // then
        assertThat(course)
                .get()
                .returns(100, Course::id)
                .returns("foo", Course::name)
                .returns(2, Course::limit)
                .extracting(Course::enrollments, LIST)
                .hasSize(3);
    }


    /*
    @Test
    void test_update() {
        // given
        jdbcTemplate.update("INSERT INTO course VALUES(100, 'dummy', 2)");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(1, 100, 1, 1, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(2, 100, 2, 2, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(3, 100, 3, 3, 'W')");



        // when
        repository.findById(100).get().new CourseUpdate(
                100,
                newLimit,
                toBeAdded,
                toBeRemoved,
                toBeActivated,
                emptyList()));

        // then
        assertThat(repository.course(100))
                .returns(4, Course::limit)
                .returns(emptyList(), Course::waitingList)
                .extracting(Course::activeList, list(Enrollment.class))
                .contains(new Enrollment(2,2,2) , atIndex(0))
                .contains(new Enrollment(3,3,3) , atIndex(1))
                .element(2)
                .returns(4, Enrollment::studentId)
                .returns(4, Enrollment::orderId);
    }

 */
}
