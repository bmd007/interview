package game.score.tracker.domain;

public record Score(int levelId, int userId, Integer value) implements Comparable<Score> {

    @Override
    public int compareTo(Score other) {
        return other.value.compareTo(this.value);
    }

    public String toCSV() {
        return "%s:%s".formatted(userId, value);
    }
}

