package wonderland.interview.risk.decision.service;

import wonderland.interview.risk.decision.api.CreditRequestDecisionV1;
import wonderland.interview.risk.decision.api.CreditRequestV1;
import wonderland.interview.risk.decision.domain.CreditDecision;
import wonderland.interview.risk.decision.domain.CreditDecisionMaker;
import wonderland.interview.risk.decision.domain.CreditHistoryRepository;
import wonderland.interview.risk.decision.domain.CustomerDebt;
import wonderland.interview.risk.decision.domain.CustomerDebtRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreditDecisionServiceTest {

    @Mock
    private CustomerDebtRepository customerDebtRepository;

    @Mock
    private CreditHistoryRepository creditHistoryRepository;

    @Mock
    private CreditDecisionMaker creditDecisionMaker;

    @InjectMocks
    private CreditDecisionServiceV1 creditDecisionService;

    @Test
    public void shouldAcceptCreditRequest() {
        CreditRequestV1 creditRequest = defaultCreditRequestOfPurchaseAmount(10);

        when(customerDebtRepository.fetchCustomerDebtForEmail(creditRequest.getEmail()))
                .thenReturn(new CustomerDebt(creditRequest.getEmail(), 7));
        when(creditDecisionMaker.makeCreditDecision(10, 7))
                .thenReturn(CreditDecision.ACCEPTED);

        CreditRequestDecisionV1 decision = creditDecisionService.handleCreditRequestV1(creditRequest);
        assertThat(decision.isAccepted(), is(true));
        assertThat(decision.getReason(), is("ok"));
    }

    @Test
    public void shouldRejectCreditRequest() {
        CreditRequestV1 creditRequest = defaultCreditRequestOfPurchaseAmount(11);

        when(customerDebtRepository.fetchCustomerDebtForEmail(creditRequest.getEmail()))
                .thenReturn(new CustomerDebt(creditRequest.getEmail(), 7));
        when(creditDecisionMaker.makeCreditDecision(11, 7))
                .thenReturn(CreditDecision.MAX_AMOUNT_BREACH);

        CreditRequestDecisionV1 decision = creditDecisionService.handleCreditRequestV1(creditRequest);
        assertThat(decision.isAccepted(), is(false));
        assertThat(decision.getReason(), is("amount"));
    }

    @Test
    public void shouldUpdateCustomerDebtWhenCreditAccepted() {
        CreditRequestV1 creditRequest = defaultCreditRequestOfPurchaseAmount(10);

        when(customerDebtRepository.fetchCustomerDebtForEmail(creditRequest.getEmail()))
                .thenReturn(new CustomerDebt(creditRequest.getEmail(), 7));
        when(creditDecisionMaker.makeCreditDecision(10, 7))
                .thenReturn(CreditDecision.ACCEPTED);

        creditDecisionService.handleCreditRequestV1(creditRequest);

        ArgumentCaptor<CustomerDebt> captor = ArgumentCaptor.forClass(CustomerDebt.class);
        verify(customerDebtRepository).persistCustomerDebt(captor.capture());
        assertThat(captor.getValue().getDebtAmount(), is(17));
    }

    private CreditRequestV1 defaultCreditRequestOfPurchaseAmount(int amount) {
        return new CreditRequestV1("john@doe.com", "John", "Doe", amount);
    }

}
