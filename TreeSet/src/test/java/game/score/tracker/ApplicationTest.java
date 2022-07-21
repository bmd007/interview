package game.score.tracker;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationTest {

    WebTestClient testClient = WebTestClient
            .bindToServer()
            .baseUrl("http://localhost:8081")
            .build();

    @BeforeAll
    static void beforeAll() throws IOException {
        ScoreKeeperApplication.main(new String[]{});
    }

    @Test
    void setOneScoreAndGetIt() {
        //given
        int userId = 1;
        String sessionKey = testClient.get()
                .uri(uriBuilder -> uriBuilder.path(String.valueOf(userId)).path("/login").build())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();
        int levelId = 1;
        int scoreValue = 1;
        //when
        testClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(String.valueOf(levelId))
                        .path("/score")
                        .queryParam("sessionkey", sessionKey)
                        .build())
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(String.valueOf(scoreValue))
                .exchange()
                .expectStatus()
                .isNoContent()
                .expectBody(Void.class);
        testClient.get()
                .uri(uriBuilder -> uriBuilder.path(String.valueOf(levelId)).path("/highscorelist").build())
                .exchange()
                //then
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .value(csv -> assertEquals("1:1", csv));
    }

    private final WebClient client = WebClient.builder().baseUrl("http://localhost:8081").build();


    @Test
    void setScoresAndExpectSortedResults() {
        //given
        SortedSet<Integer> scores = Collections.synchronizedSortedSet(new TreeSet<>((o1, o2) -> o2.compareTo(o1)));
        Random random = new Random();
        int level = 1;
        //when
        Mono<List<Integer>> expectedScoreListMono = Flux.interval(Duration.ofMillis(100))
                .take(Duration.ofSeconds(4))
                .flatMap(userId -> client.get()
                        .uri(uriBuilder -> uriBuilder.path(String.valueOf(userId)).path("/login").build())
                        .retrieve()
                        .bodyToMono(String.class))
                .flatMap(sessionKey -> {
                            int score = Math.abs(random.nextInt());
                            scores.add(score);
                            return client.post()
                                    .uri(uriBuilder -> uriBuilder
                                            .path(String.valueOf(level))
                                            .path("/score")
                                            .queryParam("sessionkey", sessionKey)
                                            .build())
                                    .contentType(MediaType.TEXT_PLAIN)
                                    .bodyValue(String.valueOf(score))
                                    .retrieve()
                                    .bodyToMono(Void.class);
                        }
                )
                .count()
                .flatMap(unused -> client.get()
                        .uri(uriBuilder -> uriBuilder.path(String.valueOf(level)).path("/highscorelist").build())
                        .retrieve()
                        .bodyToMono(String.class))
                .map(csv -> Arrays.stream(csv.split(","))
                        .map(pair -> pair.split(":")[1])
                        .map(Integer::parseInt)
                        .toList());
        StepVerifier.create(expectedScoreListMono)
                .assertNext(actualScores -> assertEquals(scores.stream().limit(15).toList(), actualScores))
                .expectComplete()
                .verify();
    }
}
