package wonderland.interview.risk.decision.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

class CreditHistoryRepositoryTest {

    private CreditHistoryRepository creditHistoryRepository;

    @BeforeEach
    void setUp() {
        creditHistoryRepository = new CreditHistoryRepositoryImpl();
    }

    @AfterEach
    void tearDown() {
        creditHistoryRepository = null;
    }
}
