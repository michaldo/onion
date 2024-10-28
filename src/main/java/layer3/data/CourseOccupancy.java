package layer3.data;

public record CourseOccupancy(
    int id,
    String name,
    int limit,
    int activeCount,
    int waitingCount
){}
