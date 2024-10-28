package onion3fusion.domain.entities;

public record CourseOccupancy(
    int id,
    String name,
    int limit,
    Long activeCount,
    Long waitingCount,
    int nextOrderId
){}
