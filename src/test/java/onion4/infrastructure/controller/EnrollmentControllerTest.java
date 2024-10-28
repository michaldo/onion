package onion4.infrastructure.controller;

import onion4.application.CourseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean CourseService courseService;

    @Test
    void test_enroll() throws Exception {
        // given
        when(courseService.enroll(100, 1)).thenReturn("A");

        // when-then
        mockMvc.perform(patch(
                "/enroll?course-id=100&student-id=1"))
                .andExpect(content().string("A"));

    }

    @Test
    void test_leave() throws Exception {
        // when
        mockMvc.perform(patch("/leave?course-id=100&student-id=1"))
                .andExpect(status().isOk());

        // then
        verify(courseService).leave(100, 1);
    }

}
