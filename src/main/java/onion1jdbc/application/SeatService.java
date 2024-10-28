package onion1jdbc.application;

import onion1jdbc.application.seat.CourseOccupancy;
import onion1jdbc.application.seat.SeatAssigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.SequencedMap;

@Service
public class SeatService {

    private static final Logger logger = LoggerFactory.getLogger(SeatService.class);

    @Autowired CourseRepository courseRepository;
    @Autowired CourseService courseService;

    public void addSeats(int numberOfSeats) {
        List<CourseOccupancy> occupancies = courseRepository.courseOccupancies();
        logger.info("{}", occupancies);
        // Map<courseId, new seats>
        SequencedMap<Integer, Integer> seatsAssignment = SeatAssigner.assign(numberOfSeats, occupancies);
        courseService.addSeats(seatsAssignment);
    }
}
