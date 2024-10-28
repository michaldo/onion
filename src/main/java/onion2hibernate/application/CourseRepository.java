package onion2hibernate.application;

import onion2hibernate.application.seat.CourseOccupancy;
import onion2hibernate.domain.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CourseRepository extends CrudRepository<Course, Integer> {

    @Query("""
            SELECT new onion2hibernate.application.seat.CourseOccupancy(c.id, c.name, c.limit, 
            COUNT(*) FILTER ( WHERE e.status = 'A' ) ,
            COUNT(*) FILTER ( WHERE e.status = 'W' ))
            FROM Course c LEFT JOIN Enrollment e ON c.id = e.courseId
            GROUP BY c.id, c.name
            """)
    List<CourseOccupancy> courseOccupancies();


}
