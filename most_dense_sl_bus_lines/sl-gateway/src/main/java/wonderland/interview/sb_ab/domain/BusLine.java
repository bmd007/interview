package wonderland.interview.sb_ab.domain;


import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class BusLine implements Comparable<BusLine> {

    String number;
    Set<String> stopNumbers = new HashSet<>();

    public BusLine(String number) {
        this.number = number;
    }

    public void addStop(String stopNumber) {
        stopNumbers.add(stopNumber);
    }

    public Integer numberOfStops() {
        return stopNumbers.size();
    }

    @Override
    public int compareTo(BusLine other) {
        return other.numberOfStops().compareTo(numberOfStops());
    }
}
