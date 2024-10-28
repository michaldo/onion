package onion1jdbc.domain;

public record Enrollment(
        Integer id,
        int studentId,
        String status) {

    public boolean isActive() {
        return status.equals("A");
    }
}
