package wonderland.interview.risk.decision.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
