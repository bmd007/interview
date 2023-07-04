package wonderland.interview.risk.decision.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.util.UUID;

public class Transaction {

    private String id;
    private int purchaseAmount;
    private CreditDecision creditDecision;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("purchaseAmount", purchaseAmount)
                .add("creditDecision", creditDecision)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return purchaseAmount == that.purchaseAmount && Objects.equal(id, that.id) && creditDecision == that.creditDecision;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, purchaseAmount, creditDecision);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Transaction(String id, int purchaseAmount, CreditDecision creditDecision) {
        this.id = id;
        this.purchaseAmount = purchaseAmount;
        this.creditDecision = creditDecision;
    }

    public Transaction(int purchaseAmount, CreditDecision creditDecision) {
        this.id = UUID.randomUUID().toString();
        this.purchaseAmount = purchaseAmount;
        this.creditDecision = creditDecision;
    }
}
