package onion2hibernate.application.seat;

public record CourseOccupancy(
    int id,
    String name,
    int limit,
    Long activeCount,
    Long waitingCount
){}
