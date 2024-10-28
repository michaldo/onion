package onion3fusion.application.seat;

import onion3fusion.domain.entities.CourseOccupancy;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;

public class SeatAssigner {

    public static LinkedHashMap<Integer, Integer> assign(int seats, List<CourseOccupancy> courseOccupancies) {

        LinkedHashMap<Integer, Integer> assignedSeats = new LinkedHashMap<>();
        if (courseOccupancies.isEmpty()) {
            return assignedSeats;
        }
        List<CourseOccupancy> sorted = courseOccupancies
                .stream()
                .sorted(Comparator
                        .comparing(
                                CourseOccupancy::waitingCount).reversed()
                        .thenComparing(
                                CourseOccupancy::activeCount)
                        .thenComparing(
                                CourseOccupancy::limit))
                .toList();


        for (CourseOccupancy courseOccupancy : sorted) {
            if (seats <= 0 || courseOccupancy.waitingCount() == 0) {
                break;
            }
            int toBeAssigned = (int) Math.min(seats, courseOccupancy.waitingCount());
            assignedSeats.put(courseOccupancy.id(), toBeAssigned);
            seats = seats - toBeAssigned;
        }
        if (seats > 0) {
            CourseOccupancy firstCourse = sorted.getFirst();
            Integer alreadyAssigned = assignedSeats.get(firstCourse.id());
            assignedSeats.put(firstCourse.id(), alreadyAssigned == null ? seats : alreadyAssigned + seats);
        }
        return assignedSeats;
    }
}
