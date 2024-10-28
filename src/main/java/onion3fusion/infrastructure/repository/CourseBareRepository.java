package onion3fusion.infrastructure.repository;

import onion3fusion.domain.CourseLeaveAggregate;
import onion3fusion.domain.entities.CourseBare;
import onion3fusion.domain.entities.CourseOccupancy;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

interface CourseBareRepository extends CrudRepository<CourseBare, Integer> {

    @Query("""
            SELECT new onion3fusion.domain.entities.CourseOccupancy(c.id, c.name, c.limit, 
            COUNT(*) FILTER ( WHERE e.status = 'A' ),
            COUNT(*) FILTER ( WHERE e.status = 'W' ),
            COALESCE( MAX(e.orderId), 0) + 1)
            FROM CourseBare c LEFT JOIN Enrollment e ON c.id = e.courseId
            WHERE c.id = :courseId
            GROUP BY c.id, c.name
            """)
    CourseOccupancy courseOccupancy(int courseId);

    @Query("""
            SELECT new onion3fusion.domain.entities.CourseOccupancy(c.id, c.name, c.limit, 
            COUNT(*) FILTER ( WHERE e.status = 'A' ),
            COUNT(*) FILTER ( WHERE e.status = 'W' ),
            COALESCE( MAX(e.orderId), 0) + 1)
            FROM CourseBare c LEFT JOIN Enrollment e ON c.id = e.courseId
            GROUP BY c.id, c.name
            """)
    List<CourseOccupancy> courseOccupancies();

    @Query("""
    WITH
        cteFirstWaiting AS (
            SELECT id as id
            FROM Enrollment 
            WHERE courseId = :courseId
            AND status = 'W'
            ORDER BY orderId
            LIMIT 1)
    SELECT new onion3fusion.domain.CourseLeaveAggregate(course, existing, firstWaiting)
    FROM CourseBare course 
        LEFT OUTER JOIN  Enrollment existing 
            ON course.id = existing.courseId AND existing.studentId = :studentId
        LEFT OUTER JOIN  Enrollment firstWaiting
             ON course.id = firstWaiting.courseId AND firstWaiting.id IN (SELECT id from cteFirstWaiting)
    WHERE course.id = :courseId
    """)
    CourseLeaveAggregate courseLeaveAggregate(int courseId, int studentId);


}
