import java.util.*;

public class UsernameChecker {

    private HashMap<String, Integer> usernameMap;
    private HashMap<String, Integer> attemptFrequency;

    public UsernameChecker() {
        usernameMap = new HashMap<>();
        attemptFrequency = new HashMap<>();
    }

    public boolean checkAvailability(String username) {
        attemptFrequency.put(username, attemptFrequency.getOrDefault(username, 0) + 1);
        return !usernameMap.containsKey(username);
    }

    public void registerUser(String username, int userId) {
        usernameMap.put(username, userId);
    }

    public List<String> suggestAlternatives(String username) {
        List<String> suggestions = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            String suggestion = username + i;
            if (!usernameMap.containsKey(suggestion)) {
                suggestions.add(suggestion);
            }
        }

        if (username.contains("_")) {
            String alt = username.replace("_", ".");
            if (!usernameMap.containsKey(alt)) {
                suggestions.add(alt);
            }
        }

        return suggestions;
    }

    public String getMostAttempted() {
        String result = null;
        int max = 0;

        for (Map.Entry<String, Integer> entry : attemptFrequency.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                result = entry.getKey();
            }
        }

        return result;
    }

    public static void main(String[] args) {

        UsernameChecker checker = new UsernameChecker();

        checker.registerUser("sushrith_sai", 1);
        checker.registerUser("sushrith_admin", 2);

        System.out.println("sushrith_sai available? " + checker.checkAvailability("sushrith_sai"));
        System.out.println("sushrith_dev available? " + checker.checkAvailability("sushrith_dev"));

        System.out.println("Suggestions for sushrith_sai: " + checker.suggestAlternatives("sushrith_sai"));

        checker.checkAvailability("sushrith_admin");
        checker.checkAvailability("sushrith_admin");
        checker.checkAvailability("sushrith_admin");

        System.out.println("Most attempted username: " + checker.getMostAttempted());
    }
}
