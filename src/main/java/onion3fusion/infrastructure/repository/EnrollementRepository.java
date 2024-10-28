package onion3fusion.infrastructure.repository;

import onion3fusion.domain.entities.Enrollment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

interface EnrollementRepository extends CrudRepository<Enrollment, Integer> {

    @Query("""
        FROM Enrollment 
        WHERE courseId = :courseId
        AND status = 'W'
        ORDER BY orderId
        LIMIT :limit      
""")
    List<Enrollment> firstNWaiting(int courseId, int limit);

    Optional<Enrollment> findByCourseIdAndStudentId(int courseId, int studentId);
}
