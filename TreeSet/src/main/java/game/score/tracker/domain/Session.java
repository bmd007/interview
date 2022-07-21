package game.score.tracker.domain;

import java.time.Duration;
import java.time.LocalDateTime;

public record Session(String id, LocalDateTime createdAt, int userId) {
    public static Session createNew(String id, int userId) {
        return new Session(id, LocalDateTime.now(), userId);
    }

    public boolean isValid() {
        return createdAt.plus(Duration.ofMinutes(10)).isAfter(LocalDateTime.now());
    }
}
