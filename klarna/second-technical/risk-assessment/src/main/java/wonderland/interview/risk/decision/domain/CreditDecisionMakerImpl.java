package wonderland.interview.risk.decision.domain;

/**
 * The implementation of the {@link CreditDecisionMaker} interface.
 */
public class CreditDecisionMakerImpl implements CreditDecisionMaker {

    public static final int PURCHASE_AMOUNT_LIMIT = 10;
    public static final int DEBT_LIMIT = 100;

    @Override
    public CreditDecision makeCreditDecision(int purchaseAmount, int currentCustomerDebt) {
        if (purchaseAmount < 0) {
            throw new IllegalArgumentException();
        }
        if (purchaseAmount > PURCHASE_AMOUNT_LIMIT) {
            return CreditDecision.MAX_AMOUNT_BREACH;
        }
        if (purchaseAmount + currentCustomerDebt > DEBT_LIMIT) {
            return CreditDecision.DEBT;
        }
        return CreditDecision.ACCEPTED;
    }

}
