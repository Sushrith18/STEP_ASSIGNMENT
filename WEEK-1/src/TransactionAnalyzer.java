import java.util.*;

class Transaction {

    int id;
    int amount;
    String merchant;
    String account;
    long timestamp;

    public Transaction(int id, int amount, String merchant, String account, long timestamp) {
        this.id = id;
        this.amount = amount;
        this.merchant = merchant;
        this.account = account;
        this.timestamp = timestamp;
    }
}

public class TransactionAnalyzer {

    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    // Classic Two-Sum
    public void findTwoSum(int target) {

        HashMap<Integer, Transaction> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                Transaction other = map.get(complement);

                System.out.println("Two-Sum Match → (" +
                        other.id + ", " + t.id + ")");
            }

            map.put(t.amount, t);
        }
    }

    // Two-Sum within 1 hour window
    public void findTwoSumWithinTime(int target) {

        HashMap<Integer, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            int complement = target - t.amount;

            if (map.containsKey(complement)) {

                for (Transaction prev : map.get(complement)) {

                    long diff = Math.abs(t.timestamp - prev.timestamp);

                    if (diff <= 3600000) { // 1 hour

                        System.out.println("Time Window Match → (" +
                                prev.id + ", " + t.id + ")");
                    }
                }
            }

            map.putIfAbsent(t.amount, new ArrayList<>());
            map.get(t.amount).add(t);
        }
    }

    // Duplicate detection
    public void detectDuplicates() {

        HashMap<String, List<Transaction>> map = new HashMap<>();

        for (Transaction t : transactions) {

            String key = t.amount + "-" + t.merchant;

            map.putIfAbsent(key, new ArrayList<>());
            map.get(key).add(t);
        }

        for (Map.Entry<String, List<Transaction>> entry : map.entrySet()) {

            if (entry.getValue().size() > 1) {

                System.out.println("Duplicate detected for " +
                        entry.getKey());

                for (Transaction t : entry.getValue()) {
                    System.out.println("Transaction ID: " + t.id +
                            " Account: " + t.account);
                }
            }
        }
    }

    // K-Sum
    public void findKSum(int k, int target) {

        List<Integer> amounts = new ArrayList<>();

        for (Transaction t : transactions) {
            amounts.add(t.amount);
        }

        Collections.sort(amounts);

        kSumHelper(amounts, k, target, 0, new ArrayList<>());
    }

    private void kSumHelper(List<Integer> nums, int k, int target,
                            int start, List<Integer> path) {

        if (k == 2) {

            HashSet<Integer> set = new HashSet<>();

            for (int i = start; i < nums.size(); i++) {

                int complement = target - nums.get(i);

                if (set.contains(complement)) {

                    List<Integer> result = new ArrayList<>(path);
                    result.add(nums.get(i));
                    result.add(complement);

                    System.out.println("K-Sum Match → " + result);
                }

                set.add(nums.get(i));
            }

            return;
        }

        for (int i = start; i < nums.size(); i++) {

            path.add(nums.get(i));

            kSumHelper(nums, k - 1,
                    target - nums.get(i),
                    i + 1, path);

            path.remove(path.size() - 1);
        }
    }

    public static void main(String[] args) {

        TransactionAnalyzer analyzer = new TransactionAnalyzer();

        long now = System.currentTimeMillis();

        analyzer.addTransaction(new Transaction(1,500,"StoreA","acc1",now));
        analyzer.addTransaction(new Transaction(2,300,"StoreB","acc2",now+1000));
        analyzer.addTransaction(new Transaction(3,200,"StoreC","acc3",now+2000));
        analyzer.addTransaction(new Transaction(4,500,"StoreA","acc4",now+3000));

        System.out.println("Two-Sum:");
        analyzer.findTwoSum(500);

        System.out.println("\nTwo-Sum within 1 hour:");
        analyzer.findTwoSumWithinTime(500);

        System.out.println("\nDuplicate Detection:");
        analyzer.detectDuplicates();

        System.out.println("\nK-Sum (k=3, target=1000):");
        analyzer.findKSum(3,1000);
    }
}
