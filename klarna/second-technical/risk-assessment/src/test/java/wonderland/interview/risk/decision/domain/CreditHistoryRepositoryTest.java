package wonderland.interview.risk.decision.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void  persistingNewTransactionWhenOneAlreadyExists(){
        //given
        Transaction transaction1 = new Transaction(1, CreditDecision.ACCEPTED);
        Transaction transaction2 = new Transaction(2, CreditDecision.ACCEPTED);
        //when
        creditHistoryRepository.persistTransaction("email", 1, CreditDecision.ACCEPTED);
        Collection transactions = creditHistoryRepository.lookupTransactions("email");
        creditHistoryRepository.persistTransaction("email", 2, CreditDecision.ACCEPTED);
        Collection updatedTransactions = creditHistoryRepository.lookupTransactions("email");
        //then
        assertTrue(transactions.contains(transaction1));
        assertTrue(updatedTransactions.containsAll(List.of(transaction1, transaction2)));
    }

}
