package game.score.tracker;

import com.sun.net.httpserver.HttpServer;
import game.score.tracker.domain.Score;
import game.score.tracker.domain.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ScoreKeeperApplication {

    private static final ConcurrentHashMap<String, Session> sessions = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Score>> scores = new ConcurrentHashMap<>();
    public static final Pattern userIdPathVariableInLoginEndpointPattern = Pattern.compile("/(?<userId>\\d+)/login");
    public static final Pattern levelIdPathVariableInScoreEndpointPattern = Pattern.compile("/(?<levelId>\\d+)/score");
    public static final Pattern levelIdPathVariableInHighScoreListEndpointPattern = Pattern.compile("/(?<levelId>\\d+)/highscorelist");

    public static void main(String[] args) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(8081), 0);
        httpServer.setExecutor(Executors.newScheduledThreadPool(200));
        httpServer.createContext("/", exchange -> {
            try {
                String requestPath = exchange.getRequestURI().getPath();
                String requestMethod = exchange.getRequestMethod();

                if (requestMethod.equals("GET") && requestPath.contains("/login")) {

                    Matcher matcher = userIdPathVariableInLoginEndpointPattern.matcher(requestPath);
                    if (!matcher.matches()){
                        throw new IllegalArgumentException("check path variable");
                    }
                    int userId = Integer.parseInt(matcher.group("userId"));
                    String sessionId = UUID.randomUUID().toString();
                    sessions.put(sessionId, Session.createNew(sessionId, userId));
                    exchange.sendResponseHeaders(200, sessionId.length());
                    exchange.getResponseBody().write(sessionId.getBytes(StandardCharsets.UTF_8));

                } else if (requestMethod.equals("POST") && requestPath.contains("/score")) {
                    var matcher = levelIdPathVariableInScoreEndpointPattern.matcher(requestPath);
                    if (!matcher.matches()){
                        throw new IllegalArgumentException("check path variable");
                    }
                    int levelId = Integer.parseInt(matcher.group("levelId"));
                    Integer userId = Optional.ofNullable(queryParamsToMap(exchange.getRequestURI().getQuery()).get("sessionkey"))
                            .map(sessions::get)
                            .filter(Session::isValid)
                            .map(Session::userId)
                            .orElseThrow(() -> new AccessDeniedException());
                    int scoreValue = Integer.parseInt(new BufferedReader(new InputStreamReader(exchange.getRequestBody())).readLine());
                    var levelScoreMap = Optional.ofNullable(scores.get(levelId))
                            .orElseGet(() -> new ConcurrentHashMap<>());
                    levelScoreMap.put(userId, new Score(levelId, userId, scoreValue));
                    scores.put(levelId, levelScoreMap);
                    exchange.sendResponseHeaders(200, 0);

                } else if (requestMethod.equals("GET") && requestPath.contains("/highscorelist")) {
                    var matcher = levelIdPathVariableInHighScoreListEndpointPattern.matcher(requestPath);
                    if (!matcher.matches()){
                        throw new IllegalArgumentException("check path variable");
                    }
                    int levelId = Integer.parseInt(matcher.group("levelId"));
                    String top15Scored = Optional.ofNullable(scores.get(levelId))
                            .stream()
                            .flatMap(levelScoreMap -> levelScoreMap.values().stream())
                            .sorted()
                            .limit(15)
                            .map(Score::toCSV)
                            .collect(Collectors.joining(","));
                    exchange.sendResponseHeaders(200, top15Scored.length());
                    exchange.getResponseBody().write(top15Scored.getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("unsupported call");
                }

            } catch (AccessDeniedException e) {
                String responseBody = "Authentication failed, error %s".formatted(e.getMessage());
                exchange.sendResponseHeaders(403, responseBody.length());
                exchange.getResponseBody().write(responseBody.getBytes(StandardCharsets.UTF_8));
            } catch (IndexOutOfBoundsException | IllegalArgumentException e) {
                String responseBody = "bad request, error %s".formatted(e.getMessage());
                exchange.sendResponseHeaders(400, responseBody.length());
                exchange.getResponseBody().write(responseBody.getBytes(StandardCharsets.UTF_8));
            } catch (Exception e) {
                String responseBody = "unexpected problem, check the path and parameters for request %s, error %s".formatted(exchange, e.getMessage());
                exchange.sendResponseHeaders(500, responseBody.length());
                exchange.getResponseBody().write(responseBody.getBytes(StandardCharsets.UTF_8));
            } finally {
                exchange.getResponseBody().close();
            }
        });
        httpServer.start();
    }

    public static Map<String, String> queryParamsToMap(String query) {
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
