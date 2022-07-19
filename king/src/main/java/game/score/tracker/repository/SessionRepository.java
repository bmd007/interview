package game.score.tracker.repository;

import game.score.tracker.domain.Session;

import java.util.concurrent.ConcurrentHashMap;

public class SessionRepository {

    public final ConcurrentHashMap<String, Session> sessionStore = new ConcurrentHashMap<>();


}
