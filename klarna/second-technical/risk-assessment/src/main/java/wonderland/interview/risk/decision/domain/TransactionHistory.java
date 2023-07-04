package wonderland.interview.risk.decision.domain;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.Streams;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransactionHistory {

    private String accountOwner;
    private List<Transaction> transactions = List.of();

    public static TransactionHistory empty(String email){
        return new TransactionHistory(email, new ArrayList<>());
    }

    public TransactionHistory addTransaction(int purchaseAmount, CreditDecision creditDecision){
        Transaction newTransaction = new Transaction(purchaseAmount, creditDecision);
        if (transactions == null){
            return new TransactionHistory(this.accountOwner, List.of(newTransaction));
        }
        List<Transaction> newTransactionHistory = Streams.concat(transactions.stream(), Stream.of(newTransaction))
                .collect(Collectors.toUnmodifiableList());
        return new TransactionHistory(this.accountOwner, newTransactionHistory);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("accountOwner", accountOwner)
                .add("transactions", transactions)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionHistory that = (TransactionHistory) o;
        return Objects.equal(accountOwner, that.accountOwner) && Objects.equal(transactions, that.transactions);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(accountOwner, transactions);
    }

    public String getAccountOwner() {
        return accountOwner;
    }

    public void setAccountOwner(String accountOwner) {
        this.accountOwner = accountOwner;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public TransactionHistory(String accountOwner, List<Transaction> transactions) {
        this.accountOwner = accountOwner;
        this.transactions = transactions;
    }
}
