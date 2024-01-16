package wonderland.interview.sb_ab.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import java.util.Set;

public record JourneyPatternPointOnLineResponse(@JsonProperty("StatusCode") int statusCode,
                                                @JsonProperty("Message") @Nullable String message,
                                                @JsonProperty("ResponseData") ResponseData responseData) {

    public record ResponseData(@JsonProperty("Result") Set<LineStopMapping> lineStopMappings) {
    }

    public record LineStopMapping(@JsonProperty("LineNumber") String lineNumber,
                                  @JsonProperty("DirectionCode") String directionCode,
                                  @JsonProperty("JourneyPatternPointNumber") String journeyPatternPointNumber) {
        public String getStopNumber() {
            return journeyPatternPointNumber;
        }
    }
}
