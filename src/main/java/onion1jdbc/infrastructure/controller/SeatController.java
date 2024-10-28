package onion1jdbc.infrastructure.controller;

import onion1jdbc.application.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class SeatController {

    @Autowired SeatService seatService;

    @PostMapping("/seat")
    void addSeats(@RequestParam("seats") int seatsNumber) {
        seatService.addSeats(seatsNumber);
    }
}
