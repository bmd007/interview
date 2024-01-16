package wonderland.interview.sb_ab.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import java.util.List;

public record BusStopsResponse(@JsonProperty("StatusCode") int statusCode,
                               @JsonProperty("Message") @Nullable String message,
                               @JsonProperty("ResponseData") ResponseData responseData) {

    public record ResponseData(@JsonProperty("Result") List<Stop> stops) {
    }

    public record Stop(@JsonProperty("StopPointNumber") String number,
                       @JsonProperty("StopPointName") String name) {
    }
}
