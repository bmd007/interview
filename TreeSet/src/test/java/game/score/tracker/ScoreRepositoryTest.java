package game.score.tracker;

import game.score.tracker.domain.Score;
import game.score.tracker.repository.ScoreRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScoreRepositoryTest {

    private final ScoreRepository scoreRepository = new ScoreRepository();

    @Test
    void setScoreForLevel() {
        //given
        int levelId = 1;
        int userId = 1;
        int score = 1;
        //when
        scoreRepository.setScore(levelId, userId, score);
        List<Score> actualScores = scoreRepository.getTop15ScoresForLevel(levelId).toList();
        //then
        assertEquals(1, actualScores.size());
        assertEquals(new Score(levelId, userId, score), actualScores.get(0));
    }

    @Test
    void setNotSortedScoresForLevelAndExpectSorted() {
        //given
        Score score1 = new Score(1, 1, 1);
        Score score2 = new Score(1, 2, 2);
        Score score20 = new Score(1, 20, 20);
        Score score12 = new Score(1, 12, 12);
        Score score10 = new Score(1, 10, 10);
        //when
        scoreRepository.setScore(score1);
        scoreRepository.setScore(score2);
        scoreRepository.setScore(score20);
        scoreRepository.setScore(score12);
        scoreRepository.setScore(score10);
        List<Score> actualScores = scoreRepository.getTop15ScoresForLevel(1).toList();
        //then
        assertEquals(5, actualScores.size());
        assertEquals(score20, actualScores.get(0));
        assertEquals(score12, actualScores.get(1));
        assertEquals(score10, actualScores.get(2));
        assertEquals(score2, actualScores.get(3));
        assertEquals(score1, actualScores.get(4));
    }

    @Test
    void setNotSortedScoresForLevelAndExpectSortedMultiThreaded() throws InterruptedException {
        //given
        Score score1 = new Score(1, 1, 1);
        Score score2 = new Score(1, 2, 2);
        Score score20 = new Score(1, 20, 20);
        Score score12 = new Score(1, 12, 12);
        Score score10 = new Score(1, 10, 10);
        //when
        var thread1 = new Thread(() -> scoreRepository.setScore(score1));
        var thread2 = new Thread(() -> scoreRepository.setScore(score2));
        var thread20 = new Thread(() -> scoreRepository.setScore(score20));
        var thread12 = new Thread(() -> scoreRepository.setScore(score12));
        var thread10 = new Thread(() -> scoreRepository.setScore(score10));

        thread2.start();
        thread10.start();
        thread20.start();
        thread12.start();
        thread1.start();

        thread1.join();
        thread2.join();
        thread10.join();
        thread12.join();
        thread20.join();

        List<Score> actualScores = scoreRepository.getTop15ScoresForLevel(1).toList();
        //then
        assertEquals(5, actualScores.size());
        assertEquals(score20, actualScores.get(0));
        assertEquals(score12, actualScores.get(1));
        assertEquals(score10, actualScores.get(2));
        assertEquals(score2, actualScores.get(3));
        assertEquals(score1, actualScores.get(4));
    }
}
