package wonderland.interview.risk.decision.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreditDecisionMakerTest {

    private CreditDecisionMaker creditDecisionMaker;

    @BeforeEach
    public void before() {
        creditDecisionMaker = new CreditDecisionMakerImpl();
    }

    @AfterEach
    public void after() {
        creditDecisionMaker = null;
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionOnNegativePurchaseAmount() {
        assertThrows(IllegalArgumentException.class, () -> creditDecisionMaker.makeCreditDecision(-42, 0));
    }

    @Test
    public void shouldAcceptCreditRequestsUpToPurchaseAmountLimit() {
        CreditDecision creditDecision = creditDecisionMaker.makeCreditDecision(10, 0);
        assertThat(creditDecision.isAccepted(), is(true));
        assertThat(creditDecision.getReason(), is("ok"));
    }

    @Test
    public void shouldAcceptCreditRequestsUpToCustomerDebtLimit() {
        CreditDecision creditDecision = creditDecisionMaker.makeCreditDecision(10, 90);
        assertThat(creditDecision.isAccepted(), is(true));
        assertThat(creditDecision.getReason(), is("ok"));
    }

    @Test
    public void shouldNotAcceptPurchasesThatWouldResultWithExceedingCustomerDebtLimit() {
        CreditDecision creditDecision = creditDecisionMaker.makeCreditDecision(1, 100);
        assertThat(creditDecision.isAccepted(), is(false));
        assertThat(creditDecision.getReason(), is("debt"));
    }

    @Test
    public void shouldNotAcceptPurchasesThatWouldResultWithExceedingPurchaseLimit() {
        CreditDecision creditDecision = creditDecisionMaker.makeCreditDecision(11, 100);
        assertThat(creditDecision.isAccepted(), is(false));
        assertThat(creditDecision.getReason(), is("amount"));
    }

}
