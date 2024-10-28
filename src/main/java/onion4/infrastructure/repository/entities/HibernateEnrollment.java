package onion4.infrastructure.repository.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import onion4.domain.CourseUpdate;

@Entity
@Table(name = "enrollment")
public class HibernateEnrollment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private int courseId;
    private int studentId;

    private int orderId;
    private String status;


    HibernateEnrollment() {}

    public HibernateEnrollment(int courseId, CourseUpdate.NewEnrollment newEnrollment) {
        this.courseId = courseId;
        this.studentId = newEnrollment.studentId();
        this.orderId = newEnrollment.orderId();
        this.status = newEnrollment.status();
    }

    public Integer id() {
        return id;
    }

    public int studentId() {
        return studentId;
    }

    public int orderId() {
        return orderId;
    }

    public String status() {
        return status;
    }

    public void activate() {
        status = "A";
    }

    public boolean isWaiting() {
        return status.equals("W");
    }
}
