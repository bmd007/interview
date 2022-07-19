package game.score.tracker.repository;

import game.score.tracker.domain.Score;

import java.util.Collections;
import java.util.Optional;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ScoreRepository {

    private static final ConcurrentHashMap<Integer, TreeSet<Score>> SCORE_STORE = new ConcurrentHashMap<>();

    public static synchronized void setScore(int levelId, int userId, int scoreValue){
        TreeSet<Score> levelScoreSet = Optional.ofNullable(SCORE_STORE.get(levelId))
                .orElseGet(() -> new TreeSet<>());
        levelScoreSet.add(new Score(levelId, userId, scoreValue));
        SCORE_STORE.put(levelId, levelScoreSet);
    }

    public static Stream<Score> getTop15ScoresForLevel(int levelId){
        return Optional.ofNullable(SCORE_STORE.get(levelId))
                .stream()
                .flatMap(levelScoreMap -> levelScoreMap.stream())
                .limit(15);
    }

}
