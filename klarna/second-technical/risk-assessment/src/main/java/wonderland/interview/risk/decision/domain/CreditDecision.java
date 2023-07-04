package wonderland.interview.risk.decision.domain;

/**
 * The predefined credit decision values.
 */
public enum CreditDecision {

    /**
     * The request was accepted.
     */
    ACCEPTED(true, "ok"),

    /**
     * The request was rejected due to high purchase amount.
     */
    MAX_AMOUNT_BREACH(false, "amount"),

    /**
     * The request was rejected due to high customer's debt.
     */
    DEBT(false, "debt");

    private final boolean accepted;

    private final String reason;

    CreditDecision(boolean accepted, String reason) {
        this.accepted = accepted;
        this.reason = reason;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public String getReason() {
        return reason;
    }
}
