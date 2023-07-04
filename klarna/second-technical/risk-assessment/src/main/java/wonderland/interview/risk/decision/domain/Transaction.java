package wonderland.interview.risk.decision.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

public class Transaction {

    private int purchaseAmount;
    private CreditDecision creditDecision;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("purchaseAmount", purchaseAmount)
                .add("creditDecision", creditDecision)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return purchaseAmount == that.purchaseAmount && creditDecision == that.creditDecision;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( purchaseAmount, creditDecision);
    }


    public int getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(int purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public CreditDecision getCreditDecision() {
        return creditDecision;
    }

    public void setCreditDecision(CreditDecision creditDecision) {
        this.creditDecision = creditDecision;
    }

    public Transaction(int purchaseAmount, CreditDecision creditDecision) {
        this.purchaseAmount = purchaseAmount;
        this.creditDecision = creditDecision;
    }
}
