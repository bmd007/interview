package game.score.tracker;

import com.sun.net.httpserver.HttpServer;
import game.score.tracker.domain.Score;
import game.score.tracker.domain.Session;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ScoreKeeperApplication {

    static final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    static final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Score>> scores = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8081), 0);
        httpServer.setExecutor(Executors.newScheduledThreadPool(200));
        httpServer.createContext("/", exchange -> {
            try {
                String requestPath = exchange.getRequestURI().getPath();

                if (exchange.getRequestMethod().equals("GET") && requestPath.contains("/login")) {
                    int userId = Integer.parseInt(requestPath.split("/*/login")[0].split("/")[1]);
                    String sessionId = UUID.randomUUID().toString();
                    sessions.put(sessionId, Session.createNew(sessionId, userId));
                    exchange.getResponseBody().write(sessionId.getBytes(StandardCharsets.UTF_8));
                    exchange.sendResponseHeaders(200, sessionId.length());

                } else if (exchange.getRequestMethod().equals("POST") && requestPath.contains("/score")) {
                    Integer userId = Optional.ofNullable(parseQueryParamsToMap(exchange.getRequestURI().getQuery()).get("sessionkey"))
                            .map(sessions::get)
                            .filter(Session::isValid)
                            .map(Session::userId)
                            .orElseThrow(() -> new AccessDeniedException());
                    int levelId = Integer.parseInt(requestPath.split("/*/score")[0].split("/")[1]);
                    int scoreValue = new DataInputStream(exchange.getRequestBody()).readInt();
                    var levelScoreMap = Optional.ofNullable(scores.get(levelId))
                            .orElseGet(() -> new ConcurrentHashMap<>());
                    levelScoreMap.put(userId, new Score(levelId, userId, scoreValue));
                    scores.put(levelId, levelScoreMap);
                    exchange.sendResponseHeaders(200, 0);

                } else if (exchange.getRequestMethod().equals("GET") && requestPath.contains("/highscorelist")) {
                    int levelId = Integer.parseInt(requestPath.split("/*/highscorelist")[0].split("/")[1]);//could have used group catching instead
                    Optional.ofNullable(scores.get(levelId))
                            .stream()
                            .flatMap(levelScoreMap -> levelScoreMap.values().stream())
                            .sorted()
                            .limit(15)
                            .map(Score::toCSV)
                            .collect(Collectors.joining(","));
                }

            } catch (AccessDeniedException e) {
                String responseBody = "Authentication failed, error %s".formatted(e.getMessage());
                exchange.getResponseBody().write(responseBody.getBytes(StandardCharsets.UTF_8));
                exchange.sendResponseHeaders(403, responseBody.length());
            } catch (Exception e) {
                String responseBody = "unexpected problem, check the path and parameters for request %s, error %s".formatted(exchange, e.getMessage());
                exchange.getResponseBody().write(responseBody.getBytes(StandardCharsets.UTF_8));
                exchange.sendResponseHeaders(500, responseBody.length());
            } finally {
                exchange.getResponseBody().close();
            }
        });
        httpServer.start();
    }

    public static Map<String, String> parseQueryParamsToMap(String query) {
        if (query == null || query.isBlank()) {
            return Map.of();
        }
        Map<String, String> queryParams = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                queryParams.put(entry[0], entry[1]);
            } else {
                queryParams.put(entry[0], "");
            }
        }
        return queryParams;
    }

}
