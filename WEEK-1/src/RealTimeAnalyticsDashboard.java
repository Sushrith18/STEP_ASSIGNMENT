import java.util.*;

class PageViewEvent {
    String url;
    String userId;
    String source;

    public PageViewEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class RealTimeAnalyticsDashboard {

    // pageUrl -> visit count
    private HashMap<String, Integer> pageViews = new HashMap<>();

    // pageUrl -> unique visitors
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();

    // traffic source -> count
    private HashMap<String, Integer> trafficSources = new HashMap<>();

    // Process incoming page view event
    public void processEvent(PageViewEvent event) {

        // Update page view count
        pageViews.put(event.url, pageViews.getOrDefault(event.url, 0) + 1);

        // Track unique visitors
        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        // Track traffic sources
        trafficSources.put(event.source,
                trafficSources.getOrDefault(event.source, 0) + 1);
    }

    // Get Top 10 pages
    public List<Map.Entry<String, Integer>> getTopPages() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        for (Map.Entry<String, Integer> entry : pageViews.entrySet()) {

            pq.offer(entry);

            if (pq.size() > 10) {
                pq.poll();
            }
        }

        List<Map.Entry<String, Integer>> result = new ArrayList<>();

        while (!pq.isEmpty()) {
            result.add(pq.poll());
        }

        Collections.reverse(result);
        return result;
    }

    // Display dashboard
    public void getDashboard() {

        System.out.println("\n===== REAL-TIME DASHBOARD =====");

        System.out.println("\nTop Pages:");

        int rank = 1;
        for (Map.Entry<String, Integer> entry : getTopPages()) {

            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.println(rank + ". " + url +
                    " - " + views + " views (" +
                    unique + " unique)");
            rank++;
        }

        System.out.println("\nTraffic Sources:");
        for (Map.Entry<String, Integer> entry : trafficSources.entrySet()) {
            System.out.println(entry.getKey() + " → " + entry.getValue());
        }
    }

    public static void main(String[] args) throws InterruptedException {

        RealTimeAnalyticsDashboard analytics =
                new RealTimeAnalyticsDashboard();

        // Simulated page view events
        analytics.processEvent(new PageViewEvent("/article/breaking-news","user_123","google"));
        analytics.processEvent(new PageViewEvent("/article/breaking-news","user_456","facebook"));
        analytics.processEvent(new PageViewEvent("/sports/championship","user_789","direct"));
        analytics.processEvent(new PageViewEvent("/sports/championship","user_123","google"));
        analytics.processEvent(new PageViewEvent("/tech/ai","user_222","google"));

        // Dashboard refresh every 5 seconds
        for (int i = 0; i < 3; i++) {

            analytics.getDashboard();

            Thread.sleep(5000);
        }
    }
}

