package layer3.data;

import org.springframework.lang.Nullable;

public record Course(
        int id,
        String name,
        int limit,
        int activeCount,
        @Nullable Integer lastOrderId) {
}
