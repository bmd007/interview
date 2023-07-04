package wonderland.interview.risk.decision.application;

import wonderland.interview.risk.decision.domain.CreditDecisionMaker;
import wonderland.interview.risk.decision.domain.CreditDecisionMakerImpl;
import wonderland.interview.risk.decision.domain.CreditHistoryRepository;
import wonderland.interview.risk.decision.domain.CreditHistoryRepositoryImpl;
import wonderland.interview.risk.decision.domain.CustomerDebtRepository;
import wonderland.interview.risk.decision.domain.CustomerDebtRepositoryImpl;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

/**
 * A class containing interface-to-implementation bindings for dependency injection
 */
public class RiskDecisionBinder extends AbstractBinder {

    @Override
    protected void configure() {
        bind(CustomerDebtRepositoryImpl.class).to(CustomerDebtRepository.class);
        bind(CreditDecisionMakerImpl.class).to(CreditDecisionMaker.class);
        bind(CreditHistoryRepositoryImpl.class).to(CreditHistoryRepository.class);
    }

}
