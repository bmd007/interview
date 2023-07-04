package wonderland.interview.risk.decision.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A class representing the details of credit request.
 */
public class CreditRequestV1 {

    /**
     * The email of the customer.
     */
    @JsonProperty(value = "email", required = true)
    private String email;

    /**
     * The total amount of this purchase.
     */
    @JsonProperty(value = "purchase_amount", required = true)
    private int purchaseAmount;

    /**
     * The first name of the customer
     */
    @JsonProperty(value = "first_name")
    private String firstName;

    /**
     * The last name of the customer
     */
    @JsonProperty(value = "last_name")
    private String lastName;

    private CreditRequestV1() {
        // Required by Jackson
    }

    public CreditRequestV1(String email, String firstName, String lastName, int purchaseAmount) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.purchaseAmount = purchaseAmount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(int purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format("CreditRequestV1 [email=%s, firstName=%s, lastName=%s, purchaseAmount=%d]", email, firstName, lastName, purchaseAmount);
    }

}
