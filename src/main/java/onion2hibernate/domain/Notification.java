package onion2hibernate.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    private int courseId;
    private int studentId;
    private String status;

    public Notification() {

    }

    public Notification(int courseId, int studentId, String status) {
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
