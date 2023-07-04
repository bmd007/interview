package wonderland.interview.risk.decision.domain;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * The implementation of the {@link CreditHistoryRepository} interface.
 */
public class CreditHistoryRepositoryImpl implements CreditHistoryRepository {

    Map<String, TransactionHistory> transactions = new ConcurrentHashMap<>();

    @Override
    public List<Transaction> lookupTransactions(String email) {
        return Optional.ofNullable(transactions.get(email))
                .stream()
                .flatMap(transactionHistory -> transactionHistory.getTransactions().stream())
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<Transaction> lookupTransactions(String email, String reason) {
        return Optional.ofNullable(transactions.get(email))
                .stream()
                .flatMap(transactionHistory -> transactionHistory.getTransactions().stream())
                .filter(transaction -> transaction.getCreditDecision().getReason().equals(reason))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public void persistTransaction(String email, int purchaseAmount, CreditDecision creditDecision) {
        TransactionHistory transactionHistory = Optional.ofNullable(transactions.get(email))
                .orElse(TransactionHistory.empty(email));
        transactions.put(email, transactionHistory.addTransaction(purchaseAmount, creditDecision));
    }
}
