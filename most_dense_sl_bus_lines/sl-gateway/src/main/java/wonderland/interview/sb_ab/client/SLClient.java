package wonderland.interview.sb_ab.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import wonderland.interview.sb_ab.client.dto.BusStopsResponse;
import wonderland.interview.sb_ab.client.dto.JourneyPatternPointOnLineResponse;
import wonderland.interview.sb_ab.configuration.SLApiProperties;

import java.util.List;

@Component
@Slf4j
public class SLClient {

    private final WebClient slWebClient;
    private final SLApiProperties slApiProperties;

    public SLClient(WebClient slWebClient, SLApiProperties slApiProperties) {
        this.slWebClient = slWebClient;
        this.slApiProperties = slApiProperties;
    }

    public Flux<JourneyPatternPointOnLineResponse.LineStopMapping> busStopsServedByLines() {
        return slWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("api2/LineData.json")
                        .queryParam("model", "JourneyPatternPointOnLine")
                        .queryParam("DefaultTransportModeCode", "BUS")
                        .queryParam("key", slApiProperties.getApiKey())
                        .build())
                .header("Accept-Encoding", "gzip")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JourneyPatternPointOnLineResponse.class)
                .flatMapIterable(response -> {
                    if (0 == response.statusCode()) {
                        return response.responseData().lineStopMappings();
                    }
                    log.error("problem {}:{} while fetching bus stops served by lines", response.statusCode(), response.message());
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "problem at fetching busStopsLineMappins");
                });
    }

    public Mono<List<BusStopsResponse.Stop>> busStops() {
        return slWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("api2/LineData.json")
                        .queryParam("model", "stop")
                        .queryParam("DefaultTransportModeCode", "BUS")
                        .queryParam("key", slApiProperties.getApiKey())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .header("Accept-Encoding", "gzip")
                .retrieve()
                .bodyToMono(BusStopsResponse.class)
                .doOnNext(response -> {
                    if (0 != response.statusCode()) {
                        log.error("problem {}:{} while fetching bus stops", response.statusCode(), response.message());
                    }
                })
                .filter(response -> 0 == response.statusCode())
                .map(response -> response.responseData().stops())
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "problem at fetching busStops")));
    }
}
