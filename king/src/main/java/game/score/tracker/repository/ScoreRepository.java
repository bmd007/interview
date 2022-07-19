package game.score.tracker.repository;

import game.score.tracker.domain.Score;

import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class ScoreRepository {

    private final ConcurrentHashMap<Integer, TreeSet<Score>> scoreStore = new ConcurrentHashMap<>();

    public synchronized void setScore(int levelId, int userId, int scoreValue){
        TreeSet<Score> levelScoreSet = Optional.ofNullable(scoreStore.get(levelId))
                .orElseGet(() -> new TreeSet<>());
        levelScoreSet.add(new Score(levelId, userId, scoreValue));
        scoreStore.put(levelId, levelScoreSet);
    }

    public Stream<Score> getTop15ScoresForLevel(int levelId){
        return Optional.ofNullable(scoreStore.get(levelId))
                .stream()
                .flatMap(levelScoreMap -> levelScoreMap.stream())
                .limit(15);
    }

}
