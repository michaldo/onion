package onion1jdbc.application.seat;

public record CourseOccupancy(
    int id,
    String name,
    int limit,
    int activeCount,
    int waitingCount
){}
