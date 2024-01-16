package wonderland.interview.sb_ab.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import wonderland.interview.sb_ab.client.dto.BusStopsResponse;
import wonderland.interview.sb_ab.client.dto.JourneyPatternPointOnLineResponse;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class SLClientTest {

    @Autowired
    private SLClient slClient;

    private final static String sampleJourneyPatternPointOnLineResponse = """
            {
              "ExecutionTime": 644,
              "Message": null,
              "ResponseData": {
                "Result": [
                  {
                    "DirectionCode": "1",
                    "ExistsFromDate": "2022-02-15 00:00:00.000",
                    "JourneyPatternPointNumber": "10008",
                    "LastModifiedUtcDateTime": "2022-02-15 00:00:00.000",
                    "LineNumber": "1"
                  },
                  {
                    "DirectionCode": "1",
                    "ExistsFromDate": "2023-03-07 00:00:00.000",
                    "JourneyPatternPointNumber": "10012",
                    "LastModifiedUtcDateTime": "2023-03-07 00:00:00.000",
                    "LineNumber": "1"
                  }
                ],
                "Type": "JourneyPatternPointOnLine",
                "Version": "2023-06-13 00:12"
              },
              "StatusCode": 0
            }
            """;

    private final static String sampleSlApiResponseStatusCode1 = """
            {
              "ExecutionTime": 1,
              "Message": "some error",
              "ResponseData": {
                "Type": "JourneyPatternPointOnLine",
                "Version": "2023-06-13 00:12"
              },
              "StatusCode": 1
            }
            """;

    private static final String sampleBusStopsResponse = """
            {
              "ExecutionTime": 588,
              "Message": null,
              "ResponseData": {
                "Result": [
                  {
                    "ExistsFromDate": "2022-10-28 00:00:00.000",
                    "LastModifiedUtcDateTime": "2022-10-28 00:00:00.000",
                    "LocationEastingCoordinate": "18.0214674159693",
                    "LocationNorthingCoordinate": "59.3373571967995",
                    "StopAreaNumber": "10001",
                    "StopAreaTypeCode": "BUSTERM",
                    "StopPointName": "Stadshagsplan",
                    "StopPointNumber": "10001",
                    "ZoneShortName": "A"
                  },
                  {
                    "ExistsFromDate": "2015-09-24 00:00:00.000",
                    "LastModifiedUtcDateTime": "2015-09-24 00:00:00.000",
                    "LocationEastingCoordinate": "18.0222866342593",
                    "LocationNorthingCoordinate": "59.3361450073188",
                    "StopAreaNumber": "10002",
                    "StopAreaTypeCode": "BUSTERM",
                    "StopPointName": "John Bergs plan",
                    "StopPointNumber": "10002",
                    "ZoneShortName": "A"
                  }
                ],
                "Type": "StopPoint",
                "Version": "2023-06-13 00:12"
              },
              "StatusCode": 0
            }
            """;

    @Test
    void busStopsServedByLines() {
        //given
        stubFor(WireMock.get(urlPathEqualTo("/api2/LineData.json"))
                .withQueryParam("model", equalTo("JourneyPatternPointOnLine"))
                .withQueryParam("DefaultTransportModeCode", equalTo("BUS"))
                .withQueryParam("key", equalTo("someKey"))
                .willReturn(okJson(sampleJourneyPatternPointOnLineResponse)));
        var lineStopMapping1 = new JourneyPatternPointOnLineResponse.LineStopMapping("1", "1", "10008");
        var lineStopMapping2 = new JourneyPatternPointOnLineResponse.LineStopMapping("1", "1", "10012");
        //when
        Flux<JourneyPatternPointOnLineResponse.LineStopMapping> busStopsServedByLines = slClient.busStopsServedByLines();
        StepVerifier.create(busStopsServedByLines)
                //then
                .expectNext(lineStopMapping1)
                .expectNext(lineStopMapping2)
                .expectComplete()
                .verify();
    }

    @Test
    void busStopsServedByLinesWhenError() {
        //given
        stubFor(WireMock.get(urlPathEqualTo("/api2/LineData.json"))
                .withQueryParam("model", equalTo("JourneyPatternPointOnLine"))
                .withQueryParam("DefaultTransportModeCode", equalTo("BUS"))
                .withQueryParam("key", equalTo("someKey"))
                .withHeader("Accept-Encoding", equalTo("gzip"))
                .willReturn(okJson(sampleSlApiResponseStatusCode1)));
        //when
        Flux<JourneyPatternPointOnLineResponse.LineStopMapping> busStopsServedByLines = slClient.busStopsServedByLines();
        StepVerifier.create(busStopsServedByLines)
                //then
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void busStopsWhenError() {
        //given
        stubFor(WireMock.get(urlPathEqualTo("/api2/LineData.json"))
                .withQueryParam("model", equalTo("stop"))
                .withQueryParam("DefaultTransportModeCode", equalTo("BUS"))
                .withQueryParam("key", equalTo("someKey"))
                .withHeader("Accept-Encoding", equalTo("gzip"))
                .willReturn(okJson(sampleSlApiResponseStatusCode1)));
        //when
        Mono<List<BusStopsResponse.Stop>> busStopsServedByLines = slClient.busStops();
        StepVerifier.create(busStopsServedByLines)
                //then
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    void busStops() {
        //given
        stubFor(WireMock.get(urlPathEqualTo("/api2/LineData.json"))
                .withQueryParam("model", equalTo("stop"))
                .withQueryParam("DefaultTransportModeCode", equalTo("BUS"))
                .withQueryParam("key", equalTo("someKey"))
                .withHeader("Accept-Encoding", equalTo("gzip"))
                .willReturn(okJson(sampleBusStopsResponse)));
        var stop1 = new BusStopsResponse.Stop("10001", "Stadshagsplan");
        var stop2 = new BusStopsResponse.Stop("10002", "John Bergs plan");
        //when
        Mono<List<BusStopsResponse.Stop>> busStopsServedByLines = slClient.busStops();
        StepVerifier.create(busStopsServedByLines)
                //then
                .expectNext(List.of(stop1, stop2))
                .expectComplete()
                .verify();
    }

}
