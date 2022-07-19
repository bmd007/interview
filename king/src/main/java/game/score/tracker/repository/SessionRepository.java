package game.score.tracker.repository;

import game.score.tracker.domain.Session;

import java.util.concurrent.ConcurrentHashMap;

public class SessionRepository {

    public static final ConcurrentHashMap<String, Session> SESSION_STORE = new ConcurrentHashMap<>();


}
