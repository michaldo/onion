package layer3.presentation;

import layer3.logic.SeatService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@WebServlet("/seat")

class SeatServlet extends HttpServlet {

    @Autowired SeatService seatService;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int seatsNumber = Integer.parseInt(req.getParameter("seats"));
        seatService.addSeats(seatsNumber);
    }

}
