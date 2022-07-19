package game.score.tracker.repository;

import game.score.tracker.domain.Score;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ScoreRepository {

    public static final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Score>> SCORE_STORE = new ConcurrentHashMap<>();

    public static synchronized void setScore(int levelId, int userId, int scoreValue){
        var levelScoreMap = Optional.ofNullable(SCORE_STORE.get(levelId))
                .orElseGet(() -> new ConcurrentHashMap<>());
        levelScoreMap.put(userId, new Score(levelId, userId, scoreValue));
        SCORE_STORE.put(levelId, levelScoreMap);
    }

    public static Stream<Score> getTop15ScoresForLevel(int levelId){
        return Optional.ofNullable(SCORE_STORE.get(levelId))
                .stream()
                .flatMap(levelScoreMap -> levelScoreMap.values().stream())
                .sorted()
                .limit(15);
    }

}
