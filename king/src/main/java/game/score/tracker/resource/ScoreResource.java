package game.score.tracker.resource;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import game.score.tracker.exception.AccessDeniedException;
import game.score.tracker.domain.Score;
import game.score.tracker.domain.Session;
import game.score.tracker.repository.ScoreRepository;
import game.score.tracker.repository.SessionRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ScoreResource implements HttpHandler {

    public static final Pattern userIdPathVariableInLoginEndpointPattern = Pattern.compile("/(?<userId>\\d+)/login");
    public static final Pattern levelIdPathVariableInScoreEndpointPattern = Pattern.compile("/(?<levelId>\\d+)/score");
    public static final Pattern levelIdPathVariableInHighScoreListEndpointPattern = Pattern.compile("/(?<levelId>\\d+)/highscorelist");
    
    private final ScoreRepository scoreRepository;
    private final SessionRepository sessionRepository;

    public ScoreResource() {
        scoreRepository = new ScoreRepository();
        sessionRepository = new SessionRepository();
    }
    public ScoreResource(ScoreRepository scoreRepository, SessionRepository sessionRepository) {
        this.scoreRepository = scoreRepository;
        this.sessionRepository = sessionRepository;
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

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String requestPath = exchange.getRequestURI().getPath();
            String requestMethod = exchange.getRequestMethod();

            if (requestMethod.equals("GET") && requestPath.contains("/login")) {

                Matcher matcher = userIdPathVariableInLoginEndpointPattern.matcher(requestPath);
                if (!matcher.matches()) {
                    throw new IllegalArgumentException("check path variable");
                }
                int userId = Integer.parseInt(matcher.group("userId"));
                String sessionId = UUID.randomUUID().toString();
                sessionRepository.sessionStore.put(sessionId, Session.createNew(sessionId, userId));
                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                exchange.sendResponseHeaders(200, sessionId.length());
                exchange.getResponseBody().write(sessionId.getBytes(StandardCharsets.UTF_8));

            } else if (requestMethod.equals("POST") && requestPath.contains("/score")) {
                var matcher = levelIdPathVariableInScoreEndpointPattern.matcher(requestPath);
                if (!matcher.matches()) {
                    throw new IllegalArgumentException("check path variable");
                }
                int levelId = Integer.parseInt(matcher.group("levelId"));
                Integer userId = Optional.ofNullable(queryParamsToMap(exchange.getRequestURI().getQuery()).get("sessionkey"))
                        .map(sessionRepository.sessionStore::get)
                        .filter(Session::isValid)
                        .map(Session::userId)
                        .orElseThrow(() -> new AccessDeniedException());
                int scoreValue = Integer.parseInt(new BufferedReader(new InputStreamReader(exchange.getRequestBody())).readLine());
                scoreRepository.setScore(levelId, userId, scoreValue);
                exchange.sendResponseHeaders(204, -1);

            } else if (requestMethod.equals("GET") && requestPath.contains("/highscorelist")) {
                var matcher = levelIdPathVariableInHighScoreListEndpointPattern.matcher(requestPath);
                if (!matcher.matches()) {
                    throw new IllegalArgumentException("check path variable");
                }
                int levelId = Integer.parseInt(matcher.group("levelId"));
                String top15Scored = scoreRepository.getTop15ScoresForLevel(levelId)
                        .map(Score::toCSV)
                        .collect(Collectors.joining(","));
                exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
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
    }
}
