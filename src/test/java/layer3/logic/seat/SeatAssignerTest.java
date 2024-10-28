package layer3.logic.seat;

import layer3.data.CourseOccupancy;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SeatAssignerTest {

    @Test
    void test() {

        LinkedHashMap<Integer, Integer> result = SeatAssigner.assign(4, List.of(
                new CourseOccupancy(100, "Match", 2, 2, 2),
                new CourseOccupancy(101, "Physics", 1, 1,1)));

        assertThat(result.get(100)).isEqualTo(3);
        assertThat(result.get(101)).isEqualTo(1);
    }
}