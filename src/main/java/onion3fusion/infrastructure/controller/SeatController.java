package onion3fusion.infrastructure.controller;

import onion3fusion.application.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SeatController {

    @Autowired SeatService seatService;

    @PostMapping("/seat")
    void addSeats(@RequestParam int seats) {
        seatService.addSeats(seats);
    }
}
