package wonderland.interview.sb_ab.dto;


import java.util.List;

public record BusLineDto(String number, List<BusStopDto> stops) {
    public int getNumberOfStops() {
        return stops.size();
    }
}
