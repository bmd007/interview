package wonderland.interview.risk.decision.service;

import com.google.common.base.Strings;
import wonderland.interview.risk.decision.api.CreditRequestDecisionV1;
import wonderland.interview.risk.decision.api.CreditRequestV1;
import wonderland.interview.risk.decision.domain.CreditDecision;
import wonderland.interview.risk.decision.domain.CreditDecisionMaker;
import wonderland.interview.risk.decision.domain.CreditHistoryRepositoryImpl;
import wonderland.interview.risk.decision.domain.CustomerDebt;
import wonderland.interview.risk.decision.domain.CustomerDebtRepository;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * The public API of the credit decision solution.
 */
@Path("/")
@Singleton
public class CreditDecisionServiceV1 {

    @Inject
    private CustomerDebtRepository customerDebtRepository;

    @Inject
    private CreditHistoryRepositoryImpl creditHistoryRepository;

    @Inject
    private CreditDecisionMaker creditDecisionMaker;

    /**
     * Handling the credit decision process.
     *
     * @param creditRequestV1 credit request with the amount and the customer's details
     * @return the decision
     */
    @POST
    @Path("/v1/decision")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CreditRequestDecisionV1 handleCreditRequestV1(CreditRequestV1 creditRequestV1) {

        performArgumentChecks(creditRequestV1);

        CustomerDebt customerDebt = customerDebtRepository.fetchCustomerDebtForEmail(creditRequestV1.getEmail());

        CreditDecision creditDecision = creditDecisionMaker.makeCreditDecision(creditRequestV1.getPurchaseAmount(), customerDebt.getDebtAmount());

        if (creditDecision.isAccepted()) {
            customerDebt.increaseDebtAmount(creditRequestV1.getPurchaseAmount());
            customerDebtRepository.persistCustomerDebt(customerDebt);
        }

        return CreditRequestDecisionV1.from(creditDecision);
    }

    private void performArgumentChecks(CreditRequestV1 creditRequest) {
        checkArgument(creditRequest != null);
        checkArgument(!Strings.isNullOrEmpty(creditRequest.getEmail()));
        checkArgument(!Strings.isNullOrEmpty(creditRequest.getFirstName()));
        checkArgument(!Strings.isNullOrEmpty(creditRequest.getLastName()));
        checkArgument(creditRequest.getPurchaseAmount() > 0);
    }

}
