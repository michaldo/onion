package layer3.logic;

import layer3.data.CourseDao;
import layer3.data.CourseOccupancy;
import layer3.logic.seat.SeatAssigner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.SequencedMap;

@Service
public class SeatService {

    private static final Logger logger = LoggerFactory.getLogger(SeatService.class);

    @Autowired CourseDao courseDao;
    @Autowired CourseService courseService;

    public void addSeats(int numberOfSeats) {
        List<CourseOccupancy> occupancies = courseDao.courseOccupancies();
        logger.info("{}", occupancies);
        // Map<courseId, new seats>
        SequencedMap<Integer, Integer> seatsAssignment = SeatAssigner.assign(numberOfSeats, occupancies);
        courseService.addSeats(seatsAssignment);
    }
}
