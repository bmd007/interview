package game.score.tracker.repository;

import game.score.tracker.domain.Score;

import java.util.concurrent.ConcurrentHashMap;

public class ScoreRepository {

    public static final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Score>> SCORE_STORE = new ConcurrentHashMap<>();

}
