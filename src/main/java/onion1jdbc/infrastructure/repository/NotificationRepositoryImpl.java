package onion1jdbc.infrastructure.repository;

import onion1jdbc.application.NotificationRepository;
import onion1jdbc.domain.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class NotificationRepositoryImpl  implements NotificationRepository {

    @Autowired private JdbcTemplate jdbcTemplate;

    @Override
    public void insertAll(List<Notification> notifications) {
        jdbcTemplate.batchUpdate("""
        
        INSERT INTO notification(course_id, student_id, status)
        VALUES (?, ?, ?)
        
        """, notifications, 100, (ps, notification) -> {
            ps.setInt(1, notification.courseId());
            ps.setInt(2, notification.studentId());
            ps.setString(3, notification.status());
        });
    }

    @Override
    public void insert(Notification notification) {
        jdbcTemplate.update("""
        
        INSERT INTO notification(course_id, student_id, status)
        VALUES (?, ?, ?)
        
        """, notification.courseId(), notification.studentId(), notification.status());
    }
}
