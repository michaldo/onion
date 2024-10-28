package layer3.presentation;

import layer3.logic.CourseService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@WebServlet("/*")
public class CourseServlet extends HttpServlet {

    @Autowired
    CourseService courseService;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String method = req.getMethod();
        if (!method.equals("PATCH")) {
            resp.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }
        String path = req.getRequestURI();
        switch (path) {
            case "/enroll" -> enroll(req, resp);
            case "/leave" -> leave(req, resp);
            default -> resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void enroll(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int courseId = Integer.parseInt(req.getParameter("course-id"));
        int studentId = Integer.parseInt(req.getParameter("student-id"));
        String result = courseService.enroll(courseId, studentId);
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getOutputStream().print(result);
    }

    private void leave(HttpServletRequest req, HttpServletResponse resp) {
        int courseId = Integer.parseInt(req.getParameter("course-id"));
        int studentId = Integer.parseInt(req.getParameter("student-id"));
        courseService.leave(courseId, studentId);
    }


}
