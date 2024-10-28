package layer3.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CourseDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<CourseOccupancy> courseOccupancies() {
        return jdbcTemplate.query("""
            SELECT c.id, name, c.limit, 
            COUNT(*) FILTER ( WHERE e.status = 'A' ) as activeCount,
            COUNT(*) FILTER ( WHERE e.status = 'W' ) as waitingCount
            FROM course c LEFT JOIN enrollment e ON c.id = e.course_id
            GROUP BY c.id, name
        """, DataClassRowMapper.newInstance(CourseOccupancy.class));
    }

    public Course course(int courseId) {
        return jdbcTemplate.queryForObject("""
        
        SELECT course.id, course.name, course.limit,
        COUNT(*) FILTER ( WHERE e.status = 'A' ) as activeCount,
        MAX(order_id) as lastOrderId
        FROM course left join enrollment e on course_id=course.id
        WHERE course.id = ?
        GROUP by course.id, name, course.limit
        
        """,  DataClassRowMapper.newInstance(Course.class), courseId) ;
    }

    public void updateLimit(int courseId, int newLimit) {
        jdbcTemplate.update("UPDATE course SET \"limit\" = ? WHERE id = ?", newLimit, courseId);
    }
}
