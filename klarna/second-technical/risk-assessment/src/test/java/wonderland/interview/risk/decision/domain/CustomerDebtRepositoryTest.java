package wonderland.interview.risk.decision.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class CustomerDebtRepositoryTest {

    private CustomerDebtRepository customerDebtRepository;

    @BeforeEach
    public void before() {
        customerDebtRepository = new CustomerDebtRepositoryImpl();
    }

    @AfterEach
    public void after() {
        customerDebtRepository = null;
    }

    @Test
    public void newCustomerDebtIsZero() {
        CustomerDebt customerDebt = customerDebtRepository.fetchCustomerDebtForEmail("john@doe.com");
        assertThat(customerDebt, notNullValue());
        assertThat(customerDebt.getCustomerEmail(), is("john@doe.com"));
        assertThat(customerDebt.getDebtAmount(), is(0));
    }

    @Test
    public void returningCustomerDebtIsNotZero() {
        for (int i = 0; i < 5; i++) {
            CustomerDebt customerDebt = customerDebtRepository.fetchCustomerDebtForEmail("john@doe.com");
            assertThat(customerDebt, notNullValue());
            assertThat(customerDebt.getCustomerEmail(), is("john@doe.com"));
            assertThat(customerDebt.getDebtAmount(), is(10 * i));
            customerDebt.increaseDebtAmount(10);
            customerDebtRepository.persistCustomerDebt(customerDebt);
        }
    }

}
