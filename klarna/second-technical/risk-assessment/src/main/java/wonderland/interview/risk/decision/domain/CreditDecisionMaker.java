package wonderland.interview.risk.decision.domain;

/**
 * An interface of the credit decision algorithm.
 */
public interface CreditDecisionMaker {

    /**
     * Obtain a credit decision for given credit request amount and current customer debt amount.
     *
     * @param purchaseAmount the requested credit amount
     * @param customerDebt   the customer's current dept
     * @return the credit decision
     */
    CreditDecision makeCreditDecision(int purchaseAmount, int customerDebt);

}