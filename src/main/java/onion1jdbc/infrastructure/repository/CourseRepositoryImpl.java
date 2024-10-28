package onion1jdbc.infrastructure.repository;

import onion1jdbc.application.CourseRepository;
import onion1jdbc.application.seat.CourseOccupancy;
import onion1jdbc.domain.CourseEnrollLeave;
import onion1jdbc.domain.CourseNWaiting;
import onion1jdbc.domain.CourseUpdate;
import onion1jdbc.domain.Enrollment;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CourseRepositoryImpl implements CourseRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired NamedParameterJdbcTemplate jdbcTemplateNamed;

    @Override
    public List<CourseOccupancy> courseOccupancies() {
        return jdbcTemplate.query("""
            SELECT c.id, name, c.limit, 
            COUNT(*) FILTER ( WHERE e.status = 'A' ) as activeCount,
            COUNT(*) FILTER ( WHERE e.status = 'W' ) as waitingCount
            FROM course c LEFT JOIN enrollment e ON c.id = e.course_id
            GROUP BY c.id, name
        """, DataClassRowMapper.newInstance(CourseOccupancy.class));
    }



    @Override
    public CourseEnrollLeave courseEnrollLeave(int courseId, int studentId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("courseId", courseId)
                .addValue("studentId", studentId);
        return jdbcTemplateNamed.queryForObject("""
       
        WITH firstW AS (
            SELECT *
            FROM enrollment
            WHERE status = 'W' and course_id = :courseId
            ORDER BY order_id
            LIMIT 1
        ), courseAgg AS (
            SELECT COALESCE(MAX(order_id), 0) + 1 AS nextOrderId, COUNT(*) as total
            FROM enrollment WHERE course_id = :courseId
        )
        SELECT course.id AS courseId, course.name AS name, course.limit as limit,
               existing.id AS exId, existing.student_id AS exStudentId, existing.status AS exStatus,
               firstW.id AS firstWId, firstW.student_id AS firstWStudentId, firstW.status AS firstWStatus,
               courseAgg.nextOrderId AS nextOrderId,
               courseAgg.total AS total
        FROM course
                 LEFT JOIN enrollment existing ON course.id = existing.course_id AND existing.student_id = :studentId
                 LEFT JOIN firstW ON true
                 JOIN courseAgg ON true
        WHERE course.id = :courseId
        
        """, namedParameters, this::map);
    }

    @Override
    public void apply(CourseUpdate update) {
        update.newLimit().ifPresent( newLimit ->
                jdbcTemplate.update("UPDATE course SET \"limit\" = ? WHERE id = ?", newLimit, update.id()));

        update.toBeAdded().ifPresent(newEnrollment -> jdbcTemplate.update("""

            INSERT INTO enrollment(course_id, student_id, order_id, status)
            VALUES (?, ?, ?, ?)
            """,
                update.id(), newEnrollment.studentId(), newEnrollment.orderId(), newEnrollment.status()));

        update.toBeRemoved().map(Enrollment::id).ifPresent(id ->
                jdbcTemplate.update("DELETE FROM enrollment WHERE id = ?", id));

        update.toBeActivated().forEach(enrollment ->
                jdbcTemplate.update("UPDATE enrollment SET status = 'A' WHERE id = ?", enrollment.id()));

    }

    private CourseEnrollLeave map(ResultSet rs, int rowNum) throws SQLException {

        Enrollment existing;
        int exId = rs.getInt("exId");
        if (rs.wasNull()) {
            existing = null;
        } else {
            existing = new Enrollment(exId, rs.getInt("exStudentId"), rs.getString("exStatus"));
        }
        Enrollment firstWaiting;
        int firstWId = rs.getInt("firstWId");
        if (rs.wasNull()) {
            firstWaiting = null;
        } else {
            firstWaiting = new Enrollment(firstWId, rs.getInt("firstWStudentId"), rs.getString("firstWStatus"));
        }
        return new CourseEnrollLeave(
                rs.getInt("courseId"),
                rs.getString("name"),
                rs.getInt("limit"),
                rs.getInt("total"),
                Optional.ofNullable(existing),
                Optional.ofNullable(firstWaiting),
                rs.getInt("nextOrderId"));
    }

    @Override
    public CourseNWaiting courseNWaiting(int courseId, int nWaiting) {
        List<CourseRow> rows = jdbcTemplate.query("""
       
        SELECT c.id AS courseId, c.name, c.limit, 
        e.id AS enrollmentId, e.student_id AS studentId, e.order_id AS orderId, e.status
        FROM course c 
        LEFT JOIN enrollment e ON c.id = e.course_id AND e.status = 'W'
        WHERE c.id = ?
        ORDER BY e.order_id
        FETCH FIRST ? ROWS ONLY;
        
        """, DataClassRowMapper.newInstance(CourseRow.class), courseId, nWaiting);
        if (rows.isEmpty()) {
            throw new EntityNotFoundException("No course found with id " + courseId);
        }
        List<Enrollment> waiting = new ArrayList<>();
        for (CourseRow row : rows) {
            if (row.enrollmentId == null) {
                // left join without any enrollment
                continue;
            }
            waiting.add(new Enrollment(row.enrollmentId, row.studentId, "W"));
        }
        CourseRow first = rows.getFirst();
        return new CourseNWaiting(
                first.courseId,
                first.name,
                first.limit,
                waiting);
    }

    record CourseRow(
            int courseId,
            String name,
            int limit,
            Integer enrollmentId,
            Integer studentId,
            Integer orderId,
            String status
    ) {

    }



}
