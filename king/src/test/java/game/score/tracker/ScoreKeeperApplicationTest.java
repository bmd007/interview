package game.score.tracker;

import game.score.tracker.domain.Score;
import org.junit.jupiter.api.Test;

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreKeeperApplicationTest {
    @Test
    void put5ScoresInTreeSet() {
        TreeSet<Score> scores = new TreeSet<>();
        scores.add(new Score(1,3, 3));
        scores.add(new Score(1,1, 1));
        scores.add(new Score(1,2, 2));
        scores.add(new Score(1,4, 4));
        scores.add(new Score(1,11, 11));
        assertEquals(11, scores.first().value());
        assertEquals(1, scores.last().value());
    }
}
