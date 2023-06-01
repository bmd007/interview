package wonderland.interview.testBed;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


class Transactions {

    //	"John,Doe,john@doe.com,30,TR000"
//	Additionally there is a creditLimit >= 0 defined that is equal to all the consumers.

    record Transaction(String firsName, String lastName, String email, int amount, String id) {
        public String creditTrackingKey() {
            return "%s_%s_%s".formatted(firsName, lastName, email);
        }
    }

    private static Transaction convertTransactionData(String transactionData) {
        String[] transactionParts = transactionData.split(",");
        return new Transaction(transactionParts[0],
                transactionParts[1],
                transactionParts[2],
                Integer.parseInt(transactionParts[3]),
                transactionParts[4]);
    }

    public static List<String> findRejectedTransactions(List<String> transactionsData, int creditLimit) {
        Map<String, Integer> creditTrack = new HashMap<>();
        return transactionsData.stream()
                .map(Transactions::convertTransactionData)
                .filter(transaction -> {
                    Integer currentCredit = Optional.ofNullable(creditTrack.get(transaction.creditTrackingKey())).orElse(0);
                    if (creditLimit <= 0 ||
                            transaction.amount > creditLimit ||
                            currentCredit + transaction.amount > creditLimit) {
                        return true;
                    }
                    creditTrack.put(transaction.creditTrackingKey(), transaction.amount() + currentCredit);
                    return false;
                })
                .map(Transaction::id)
                .toList();
    }


//    @Test
//    public void shouldReturnEmptyListIfThereIsNoTransactions() {
//        assertThat(Transactions.findRejectedTransactions(new ArrayList<>(), 0).size(), is(0));
//    }
//
//    @Test
//    public void shouldReturnEmptyListIfThereIsATransactionWithinCreditLimit() {
//        List<String> transactions = Arrays.asList("John,Doe,john@doe.com,200,TR0001");
//
//        List<String> rejectedTransactions = Transactions.findRejectedTransactions(transactions, 200);
//
//        assertThat(rejectedTransactions.size(), is(0));
//    }
//
//    @Test
//    public void shouldReturnTransationThatIsOverCreditLimit() {
//        List<String> transactions = Arrays.asList("John,Doe,john@doe.com,201,TR0001");
//
//        List<String> rejectedTransactions = Transactions.findRejectedTransactions(transactions, 200);
//
//        assertThat(rejectedTransactions, is(Arrays.asList("TR0001")));
//    }

}
