package onion3fusion.infrastructure.repository;

import onion3fusion.application.NotificationRepository;
import onion3fusion.domain.entities.Notification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NotificationRepositoryTest {

    @Autowired NotificationRepository repository;

    @Autowired JdbcTemplate jdbcTemplate;

    @Test
    void test_insert() {

        // when
        repository.saveAll(List.of(new Notification(100, 1, "A")));

        // then
        int count = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "notification",
                "course_id = 100 and student_id = 1 and status = 'A'");
        assertThat(count).isEqualTo(1);
    }
}
