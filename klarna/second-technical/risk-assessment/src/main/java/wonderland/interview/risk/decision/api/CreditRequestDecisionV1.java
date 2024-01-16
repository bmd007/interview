package wonderland.interview.risk.decision.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import wonderland.interview.risk.decision.domain.CreditDecision;

/**
 * A class representing credit decision result.
 */
public class CreditRequestDecisionV1 {

    /**
     * The result of the request.
     */
    @JsonProperty(value = "accepted", required = true)
    private boolean accepted;

    /**
     * The reason of the decision.
     */
    @JsonProperty(value = "reason", required = true)
    private String reason;

    private CreditRequestDecisionV1() {
        // Required by Jackson
    }

    public CreditRequestDecisionV1(boolean accepted, String reason) {
        this.accepted = accepted;
        this.reason = reason;
    }

    public boolean isAccepted() {
        return accepted;
    }

    private void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getReason() {
        return reason;
    }

    private void setReason(String reason) {
        this.reason = reason;
    }

    public static CreditRequestDecisionV1 from(CreditDecision creditDecision) {
        return new CreditRequestDecisionV1(creditDecision.isAccepted(), creditDecision.getReason());
    }

    @Override
    public String toString() {
        return String.format("CreditDecisionV1 [accepted=%s, reason=%s]", accepted, reason);
    }

}
