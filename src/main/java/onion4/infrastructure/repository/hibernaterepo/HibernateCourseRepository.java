package onion4.infrastructure.repository.hibernaterepo;


import onion4.application.seat.CourseOccupancy;
import onion4.infrastructure.repository.entities.HibernateCourse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HibernateCourseRepository extends CrudRepository<HibernateCourse, Integer> {

    @Query("""
            SELECT new onion4.application.seat.CourseOccupancy(c.id, c.name, c.limit, 
            COUNT(*) FILTER ( WHERE e.status = 'A' ),
            COUNT(*) FILTER ( WHERE e.status = 'W' ))
            FROM HibernateCourse c LEFT JOIN HibernateEnrollment e ON c.id = e.courseId
            GROUP BY c.id, c.name
            """)
    List<CourseOccupancy> courseOccupancies();

}
