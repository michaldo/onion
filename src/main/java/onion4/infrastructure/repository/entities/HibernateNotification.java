package onion4.infrastructure.repository.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "notification")
public class HibernateNotification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    private int courseId;
    private int studentId;
    private String status;

    public HibernateNotification() {

    }

    public HibernateNotification(int courseId, int studentId, String status) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.status = status;
    }

    public int courseId() {
        return courseId;
    }

    public int studentId() {
        return studentId;
    }

    public String status() {
        return status;
    }

}
