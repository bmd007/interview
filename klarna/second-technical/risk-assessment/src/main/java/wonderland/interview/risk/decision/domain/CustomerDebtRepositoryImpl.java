package wonderland.interview.risk.decision.domain;

import com.google.common.collect.Maps;

import javax.annotation.Resource;
import java.util.Map;

/**
 * The implementation of the {@link CustomerDebtRepository} interface.
 */
@Resource
public class CustomerDebtRepositoryImpl implements CustomerDebtRepository {

    private Map<String, Integer> customerDebtStorage = Maps.newConcurrentMap();

    @Override
    public CustomerDebt fetchCustomerDebtForEmail(String email) {
        return new CustomerDebt(email, customerDebtStorage.getOrDefault(email, 0));
    }

    @Override
    public void persistCustomerDebt(CustomerDebt customerDebt) {
        customerDebtStorage.put(customerDebt.getCustomerEmail(), customerDebt.getDebtAmount());
    }

}
