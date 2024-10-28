package onion4.application.seat;

public record CourseOccupancy(
    int id,
    String name,
    int limit,
    long activeCount,
    long waitingCount
){}
