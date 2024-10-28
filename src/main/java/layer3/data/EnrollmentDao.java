package layer3.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EnrollmentDao {

    @Autowired private JdbcTemplate jdbcTemplate;

    public Enrollment getEnrollment(int courseId, int studentId) {
        try {
            return jdbcTemplate.queryForObject("""
        SELECT id, course_id, student_id "studentId", order_id, status FROM enrollment e
        WHERE e.course_id = ? AND e.student_id = ?""",
                    DataClassRowMapper.newInstance(Enrollment.class) , courseId, studentId);
        } catch (EmptyResultDataAccessException e) {
            // crazy? say it to https://www.baeldung.com/jdbctemplate-fix-emptyresultdataaccessexception
            return null;
        }
    }

    public void enroll(int courseId, int studentId, int orderId, String status) {
        jdbcTemplate.update("""

        INSERT INTO enrollment(course_id, student_id, order_id, status)
        VALUES (?, ?, ?, ?)
       
        """, courseId, studentId, orderId, status);
    }

    public void remove(int id) {
        jdbcTemplate.update("DELETE FROM enrollment WHERE id = ?", id);
    }

    public List<Enrollment> findFirstNWaiting(int courseId, int n) {

        return jdbcTemplate.query("""
        
        SELECT id, course_id, student_id "studentId", order_id, status 
        FROM enrollment e
        WHERE e.course_id = ? AND e.status = 'W'
        ORDER BY e.order_id
        LIMIT ?
        
        """, DataClassRowMapper.newInstance(Enrollment.class) , courseId, n);
    }

    public void activate(int id) {
        jdbcTemplate.update("UPDATE enrollment SET status = 'A' WHERE id = ?", id);
    }

}
