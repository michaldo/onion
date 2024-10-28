package onion3fusion.infrastructure.repository;

import onion3fusion.domain.entities.CourseOccupancy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CourseBareRepositoryTest {

    @Autowired CourseBareRepository repository;

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
        CourseOccupancy occupancy = repository.courseOccupancy(100);

        // then
        assertThat(occupancy)
                .isEqualTo(new CourseOccupancy(100, "dummy", 2, 0L,0L, 1));

    }

    @Test
    void test_occupancies() {
        // given
        jdbcTemplate.update("INSERT INTO course VALUES(100, 'dummy', 2)");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(1, 100, 1, 1, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(2, 100, 2, 2, 'A')");
        jdbcTemplate.update("INSERT INTO enrollment VALUES(3, 100, 3, 3, 'W')");

        // when
        CourseOccupancy occupancies = repository.courseOccupancy(100);

        // then
        assertThat(occupancies)
                .isEqualTo(new CourseOccupancy(100, "dummy", 2, 2L,1L, 4));
    }

}