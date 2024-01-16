package wonderland.interview.sb_ab.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import wonderland.interview.sb_ab.client.SLClient;
import wonderland.interview.sb_ab.domain.BusLine;
import wonderland.interview.sb_ab.dto.BusLineDto;
import wonderland.interview.sb_ab.dto.BusStopDto;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DenseBusLinesService {

    private final SLClient slClient;
    private Map<String, BusLineDto> top10DenseBusLinesCache = new ConcurrentHashMap<>();

    public DenseBusLinesService(SLClient slClient) {
        this.slClient = slClient;
    }

    public Flux<BusLineDto> getTop10DenseBusLines() {
        if (10 == top10DenseBusLinesCache.size()){
            return Flux.fromIterable(top10DenseBusLinesCache.values());
        }

        var top10DenseLines = slClient.busStopsServedByLines()
                .collect(() -> new HashMap<String, BusLine>(), (busLineStops, lineStopMapping) -> {
                    var busLine = Optional.ofNullable(busLineStops.get(lineStopMapping.lineNumber()))
                            .orElse(new BusLine(lineStopMapping.lineNumber()));
                    busLine.addStop(lineStopMapping.getStopNumber());
                    busLineStops.put(lineStopMapping.lineNumber(), busLine);
                })
                .flatMapIterable(HashMap::values)
                .sort()
                .take(10)
                .cache();

        return slClient.busStops()
                .flatMapMany(stops ->
                        top10DenseLines.map(line -> {
                            var stopsWithName = stops.stream()
                                    .filter(stop -> line.getStopNumbers().contains(stop.number()))
                                    .map(stop -> new BusStopDto(stop.number(), stop.name()))
                                    .toList();
                            return new BusLineDto(line.getNumber(), stopsWithName);
                        })
                )
                .doOnNext(busLineDto -> top10DenseBusLinesCache.put(busLineDto.number(), busLineDto));
    }
}
