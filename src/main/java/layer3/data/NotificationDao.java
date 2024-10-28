package layer3.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotificationDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void notify(int courseId, int studentId, String status) {
        jdbcTemplate.update("""

        INSERT INTO notification(course_id, student_id, status)
        VALUES (?, ?, ?)
       
        """, courseId, studentId, status);
    }

}
