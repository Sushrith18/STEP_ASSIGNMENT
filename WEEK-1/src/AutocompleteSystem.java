import java.util.*;

class TrieNode {

    Map<Character, TrieNode> children = new HashMap<>();
    Map<String, Integer> queryFrequency = new HashMap<>();
    boolean isEnd = false;
}

public class AutocompleteSystem {

    private TrieNode root;

    // Global query frequency storage
    private HashMap<String, Integer> globalFrequency;

    public AutocompleteSystem() {
        root = new TrieNode();
        globalFrequency = new HashMap<>();
    }

    // Insert query into Trie
    public void addQuery(String query) {

        globalFrequency.put(query,
                globalFrequency.getOrDefault(query, 0) + 1);

        TrieNode node = root;

        for (char c : query.toCharArray()) {

            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);

            node.queryFrequency.put(query,
                    globalFrequency.get(query));
        }

        node.isEnd = true;
    }

    // Search suggestions by prefix
    public List<String> search(String prefix) {

        TrieNode node = root;

        for (char c : prefix.toCharArray()) {

            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }

            node = node.children.get(c);
        }

        // Use min heap for top 10 results
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry :
                node.queryFrequency.entrySet()) {

            pq.offer(entry);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<String> results = new ArrayList<>();

        while (!pq.isEmpty()) {
            results.add(pq.poll().getKey());
        }

        Collections.reverse(results);
        return results;
    }

    public static void main(String[] args) {

        AutocompleteSystem system = new AutocompleteSystem();

        system.addQuery("java tutorial");
        system.addQuery("javascript");
        system.addQuery("java download");
        system.addQuery("java tutorial");
        system.addQuery("java 21 features");

        System.out.println("Search results for 'jav':");

        List<String> suggestions = system.search("jav");

        int rank = 1;

        for (String s : suggestions) {

            System.out.println(rank + ". " + s +
                    " (" + system.globalFrequency.get(s) + " searches)");

            rank++;
        }

        // Update frequency example
        system.addQuery("java 21 features");
        system.addQuery("java 21 features");

        System.out.println("\nUpdated frequency for 'java 21 features': "
                + system.globalFrequency.get("java 21 features"));
    }
}


