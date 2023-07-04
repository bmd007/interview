package wonderland.interview.risk.decision.domain;

/**
 * An interface for handling the persistence of customer's debt.
 */
public interface CustomerDebtRepository {

    /**
     * Fetching current debt for the customer of given email address.
     *
     * @param email the primary identifier of the customer
     * @return the customer's current debt
     */
    CustomerDebt fetchCustomerDebtForEmail(String email);

    /**
     * Persisting customer's debt.
     *
     * @param customerDebt the customer's debt
     */
    void persistCustomerDebt(CustomerDebt customerDebt);

}