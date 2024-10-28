package onion1jdbc.infrastructure.controller;

import onion1jdbc.application.SeatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SeatController.class)
class SeatsControllerTest {

    @Autowired MockMvc mockMvc;

    @MockitoBean SeatService seatService;

    @Test
    void test_seat() throws Exception {
        // when-then
        mockMvc.perform(post("/seat?seats=2")).andExpect(status().isOk());

        // then
        verify(seatService).addSeats(2);
    }

}
