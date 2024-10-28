package onion2hibernate.infrastructure.controller;

import onion2hibernate.application.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EnrollmentController {

    @Autowired CourseService courseService;

    @PatchMapping("/enroll")
    String enroll(@RequestParam("course-id") int courseId, @RequestParam("student-id") int studentId) {
        return courseService.enroll(courseId, studentId);
    }

    @PatchMapping("/leave")
    void leave(@RequestParam("course-id") int courseId, @RequestParam("student-id") int studentId) {
        courseService.leave(courseId, studentId);
    }
}
