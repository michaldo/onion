package onion2hibernate.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Enrollment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private int courseId;
    private int studentId;
    private int orderId;
    private String status;

    public Enrollment(int courseId, int studentId, int orderId, String status) {
        this.courseId = courseId;
        this.studentId = studentId;
        this.orderId = orderId;
        this.status = status;
    }

    public Enrollment() {

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
