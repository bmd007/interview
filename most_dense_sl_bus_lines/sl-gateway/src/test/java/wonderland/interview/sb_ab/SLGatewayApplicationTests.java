package wonderland.interview.sb_ab;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import wonderland.interview.sb_ab.dto.BusLineDto;
import wonderland.interview.sb_ab.dto.BusStopDto;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SLGatewayApplicationTests {

    @LocalServerPort
    int port;

    @Autowired
    private WebTestClient testClient;

    private final static String sampleJourneyPatternPointOnLineResponse = """
            {
              "ExecutionTime": 644,
              "Message": null,
              "ResponseData": {
                "Result": [
                  {
                    "DirectionCode": "1",
                    "ExistsFromDate": "2022-02-15 00:00:00.000",
                    "JourneyPatternPointNumber": "10001",
                    "LastModifiedUtcDateTime": "2022-02-15 00:00:00.000",
                    "LineNumber": "1"
                  },
                  {
                    "DirectionCode": "1",
                    "ExistsFromDate": "2023-03-07 00:00:00.000",
                    "JourneyPatternPointNumber": "10002",
                    "LastModifiedUtcDateTime": "2023-03-07 00:00:00.000",
                    "LineNumber": "2"
                  }
                ],
                "Type": "JourneyPatternPointOnLine",
                "Version": "2023-06-13 00:12"
              },
              "StatusCode": 0
            }
            """;

    private final static String sampleJourneyPatternPointOnLineBigResponse = """
            {
              "ExecutionTime": 644,
              "Message": null,
              "ResponseData": {
                "Result": [
                  {
                    "DirectionCode": "1",
                    "JourneyPatternPointNumber": "10001",
                    "LineNumber": "1"
                  },
                  {
                    "DirectionCode": "1",
                    "JourneyPatternPointNumber": "10002",
                    "LineNumber": "2"
                  },
                  {
                    "DirectionCode": "1",
                    "JourneyPatternPointNumber": "10003",
                    "LineNumber": "3"
                  },
                  {
                    "DirectionCode": "1",
                    "JourneyPatternPointNumber": "10001",
                    "LineNumber": "4"
                  },
                  {
                    "DirectionCode": "1",
                    "JourneyPatternPointNumber": "10001",
                    "LineNumber": "5"
                  },
                  {
                    "DirectionCode": "1",
                    "JourneyPatternPointNumber": "10001",
                    "LineNumber": "6"
                  },
                  {
                    "DirectionCode": "1",
                    "JourneyPatternPointNumber": "10001",
                    "LineNumber": "7"
                  },
                  {
                    "DirectionCode": "1",
                    "JourneyPatternPointNumber": "10001",
                    "LineNumber": "8"
                  },
                  {
                    "DirectionCode": "1",
                    "JourneyPatternPointNumber": "10001",
                    "LineNumber": "9"
                  },
                  {
                    "DirectionCode": "1",
                    "JourneyPatternPointNumber": "10001",
                    "LineNumber": "10"
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
                  },
                  {
                    "ExistsFromDate": "2015-09-24 00:00:00.000",
                    "LastModifiedUtcDateTime": "2015-09-24 00:00:00.000",
                    "LocationEastingCoordinate": "18.0222866342593",
                    "LocationNorthingCoordinate": "59.3361450073188",
                    "StopAreaNumber": "10002",
                    "StopAreaTypeCode": "BUSTERM",
                    "StopPointName": "some stop",
                    "StopPointNumber": "10003",
                    "ZoneShortName": "A"
                  }
                ],
                "Type": "StopPoint",
                "Version": "2023-06-13 00:12"
              },
              "StatusCode": 0
            }
            """;

    @BeforeEach
    public void beforeEach() {
        WireMock.reset();
    }

    @Test
    void busStopsServedByLines() {
        //given
        stubFor(WireMock.get(urlPathEqualTo("/api2/LineData.json"))
                .withQueryParam("model", equalTo("JourneyPatternPointOnLine"))
                .withQueryParam("DefaultTransportModeCode", equalTo("BUS"))
                .withQueryParam("key", equalTo("someKey"))
                .willReturn(okJson(sampleJourneyPatternPointOnLineResponse)));
        stubFor(WireMock.get(urlPathEqualTo("/api2/LineData.json"))
                .withQueryParam("model", equalTo("stop"))
                .withQueryParam("DefaultTransportModeCode", equalTo("BUS"))
                .withQueryParam("key", equalTo("someKey"))
                .withHeader("Accept-Encoding", equalTo("gzip"))
                .willReturn(okJson(sampleBusStopsResponse)));
        var stop1 = new BusStopDto("10001", "Stadshagsplan");
        var stop2 = new BusStopDto("10002", "John Bergs plan");
        var line1 = new BusLineDto("1", List.of(stop1));
        var line2 = new BusLineDto("2", List.of(stop2));
        //when
        Flux<BusLineDto> serverSentEvents = testClient
                .get()
                .uri("/v1/bus/lines/most-dense/top10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //then
                .expectStatus()
                .isOk()
                .returnResult(BusLineDto.class)
                .getResponseBody();
        StepVerifier.create(serverSentEvents)
                //then
                .expectNext(line1)
                .expectNext(line2)
                .expectComplete()
                .verify();
        verify(1, RequestPatternBuilder.newRequestPattern(RequestMethod.GET,
                new UrlPattern(equalTo("/api2/LineData.json?model=stop&DefaultTransportModeCode=BUS&key=someKey"), false)));
        verify(1, RequestPatternBuilder.newRequestPattern(RequestMethod.GET,
                new UrlPattern(equalTo("/api2/LineData.json?model=JourneyPatternPointOnLine&DefaultTransportModeCode=BUS&key=someKey"), false)));
    }


    @Test
    void busStopsServedByLinesWhenCached() {
        //given
        WebClient webClient = WebClient.builder().baseUrl("http://localhost:%s".formatted(port)).build();
        stubFor(WireMock.get(urlPathEqualTo("/api2/LineData.json"))
                .withQueryParam("model", equalTo("JourneyPatternPointOnLine"))
                .withQueryParam("DefaultTransportModeCode", equalTo("BUS"))
                .withQueryParam("key", equalTo("someKey"))
                .willReturn(okJson(sampleJourneyPatternPointOnLineBigResponse)));
        stubFor(WireMock.get(urlPathEqualTo("/api2/LineData.json"))
                .withQueryParam("model", equalTo("stop"))
                .withQueryParam("DefaultTransportModeCode", equalTo("BUS"))
                .withQueryParam("key", equalTo("someKey"))
                .withHeader("Accept-Encoding", equalTo("gzip"))
                .willReturn(okJson(sampleBusStopsResponse)));
        var stop1 = new BusStopDto("10001", "Stadshagsplan");
        var stop2 = new BusStopDto("10002", "John Bergs plan");
        var stop3 = new BusStopDto("10003", "some stop");
        var line1 = new BusLineDto("1", List.of(stop1));
        var line2 = new BusLineDto("2", List.of(stop2));
        var line3 = new BusLineDto("3", List.of(stop3));
        var line4 = new BusLineDto("4", List.of(stop1));
        var line5 = new BusLineDto("5", List.of(stop1));
        var line6 = new BusLineDto("6", List.of(stop1));
        var line7 = new BusLineDto("7", List.of(stop1));
        var line8 = new BusLineDto("8", List.of(stop1));
        var line9 = new BusLineDto("9", List.of(stop1));
        var line10 = new BusLineDto("10", List.of(stop1));
        //when
        Flux<BusLineDto> serverSentEvents = webClient
                .get()
                .uri("/v1/bus/lines/most-dense/top10")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(BusLineDto.class)
                .repeat(3);
        //then
        StepVerifier.create(serverSentEvents)
                //then
                .expectNext(line1, line2, line3, line4, line5, line6, line7, line8, line9, line10)
                .expectNext(line1, line2, line3, line4, line5, line6, line7, line8, line9, line10)
                .expectNext(line1, line2, line3, line4, line5, line6, line7, line8, line9, line10)
                .expectNext(line1, line2, line3, line4, line5, line6, line7, line8, line9, line10)
                .expectComplete()
                .verify();
        verify(1, RequestPatternBuilder.newRequestPattern(RequestMethod.GET,
                new UrlPattern(equalTo("/api2/LineData.json?model=stop&DefaultTransportModeCode=BUS&key=someKey"), false)));
        verify(1, RequestPatternBuilder.newRequestPattern(RequestMethod.GET,
                new UrlPattern(equalTo("/api2/LineData.json?model=JourneyPatternPointOnLine&DefaultTransportModeCode=BUS&key=someKey"), false)));
    }

    @Test
    void busStopsServedByLinesWhenError() {
        //given
        stubFor(WireMock.get(urlPathEqualTo("/api2/LineData.json"))
                .withQueryParam("model", equalTo("JourneyPatternPointOnLine"))
                .withQueryParam("DefaultTransportModeCode", equalTo("BUS"))
                .withQueryParam("key", equalTo("someKey"))
                .willReturn(okJson(sampleSlApiResponseStatusCode1))
        );
        stubFor(WireMock.get(urlPathEqualTo("/api2/LineData.json"))
                .withQueryParam("model", equalTo("stop"))
                .withQueryParam("DefaultTransportModeCode", equalTo("BUS"))
                .withQueryParam("key", equalTo("someKey"))
                .withHeader("Accept-Encoding", equalTo("gzip"))
                .willReturn(okJson(sampleSlApiResponseStatusCode1)));
        //when
        testClient
                .get()
                .uri("/v1/bus/lines/most-dense/top10")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                //then
                .expectStatus()
                .isBadRequest();
        verify(1, RequestPatternBuilder.newRequestPattern(RequestMethod.GET,
                new UrlPattern(equalTo("/api2/LineData.json?model=stop&DefaultTransportModeCode=BUS&key=someKey"), false)));
        verify(0, RequestPatternBuilder.newRequestPattern(RequestMethod.GET,
                new UrlPattern(equalTo("/api2/LineData.json?model=JourneyPatternPointOnLine&DefaultTransportModeCode=BUS&key=someKey"), false)));
    }
}
