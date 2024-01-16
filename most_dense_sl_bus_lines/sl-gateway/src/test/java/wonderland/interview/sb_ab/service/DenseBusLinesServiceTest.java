package wonderland.interview.sb_ab.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import wonderland.interview.sb_ab.client.SLClient;
import wonderland.interview.sb_ab.client.dto.BusStopsResponse;
import wonderland.interview.sb_ab.client.dto.JourneyPatternPointOnLineResponse;
import wonderland.interview.sb_ab.dto.BusLineDto;
import wonderland.interview.sb_ab.dto.BusStopDto;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DenseBusLinesServiceTest {

    @Mock
    private SLClient slClient;

    @InjectMocks
    private DenseBusLinesService denseBusLinesService;

    @Test
    void top10DenseBusLines() {
        //given
        var lineStopMapping1 = new JourneyPatternPointOnLineResponse.LineStopMapping("1", "1", "1");
        var lineStopMapping2 = new JourneyPatternPointOnLineResponse.LineStopMapping("1", "1", "2");
        var lineStopMapping5 = new JourneyPatternPointOnLineResponse.LineStopMapping("1", "1", "3");
        var lineStopMapping6 = new JourneyPatternPointOnLineResponse.LineStopMapping("1", "1", "4");
        var lineStopMapping3 = new JourneyPatternPointOnLineResponse.LineStopMapping("2", "1", "1");
        var lineStopMapping4 = new JourneyPatternPointOnLineResponse.LineStopMapping("2", "1", "2");
        when(slClient.busStopsServedByLines())
                .thenReturn(Flux.just(lineStopMapping1, lineStopMapping2, lineStopMapping5, lineStopMapping3, lineStopMapping4, lineStopMapping6));
        var stop1 = new BusStopsResponse.Stop("1", "stop 1");
        var stop2 = new BusStopsResponse.Stop("2", "stop 2");
        var stop3 = new BusStopsResponse.Stop("3", "stop 3");
        var stop4 = new BusStopsResponse.Stop("4", "stop 4");
        when(slClient.busStops())
                .thenReturn(Mono.just(List.of(stop1, stop2, stop3, stop4)));
        var busStop1Dto = new BusStopDto("1", "stop 1");
        var busStop2Dto = new BusStopDto("2", "stop 2");
        var busStop3Dto = new BusStopDto("3", "stop 3");
        var busStop4Dto = new BusStopDto("4", "stop 4");
        var busLine1Dto = new BusLineDto("1", List.of(busStop1Dto, busStop2Dto, busStop3Dto, busStop4Dto));
        var busLine2Dto = new BusLineDto("2", List.of(busStop1Dto, busStop2Dto));
        //when
        Flux<BusLineDto> busLinesFlux = denseBusLinesService.getTop10DenseBusLines();
        StepVerifier.create(busLinesFlux)
                //then
                .expectNext(busLine1Dto)
                .expectNext(busLine2Dto)
                .expectComplete()
                .verify();
        verify(slClient, times(1)).busStops();
        verify(slClient, times(1)).busStopsServedByLines();
    }

    @Test
    void top10DenseBusLinesWhenNoData() {
        //given
        when(slClient.busStopsServedByLines()).thenReturn(Flux.empty());
        when(slClient.busStops()).thenReturn(Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST)));
        //when
        Flux<BusLineDto> busLinesFlux = denseBusLinesService.getTop10DenseBusLines();
        StepVerifier.create(busLinesFlux)
                //then
                .expectError(ResponseStatusException.class)
                .verify();
    }
}