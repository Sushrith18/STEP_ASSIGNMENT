import java.util.*;

class VideoData {
    String videoId;
    String content;

    public VideoData(String videoId, String content) {
        this.videoId = videoId;
        this.content = content;
    }
}

public class MultiLevelCache {

    // L1 Cache (memory) with LRU eviction
    private LinkedHashMap<String, VideoData> L1;

    // L2 Cache (simulated SSD)
    private HashMap<String, VideoData> L2;

    // L3 Database (all videos)
    private HashMap<String, VideoData> L3;

    private int L1_CAPACITY = 10000;
    private int L2_CAPACITY = 100000;

    // Statistics
    private int L1_hits = 0;
    private int L2_hits = 0;
    private int L3_hits = 0;

    public MultiLevelCache() {

        L1 = new LinkedHashMap<String, VideoData>(L1_CAPACITY,0.75f,true) {
            protected boolean removeEldestEntry(Map.Entry<String, VideoData> eldest) {
                return size() > L1_CAPACITY;
            }
        };

        L2 = new HashMap<>();
        L3 = new HashMap<>();
    }

    // Add video to database
    public void addVideoToDatabase(String videoId, String content) {
        L3.put(videoId, new VideoData(videoId, content));
    }

    // Get video
    public VideoData getVideo(String videoId) {

        // L1 lookup
        if (L1.containsKey(videoId)) {

            L1_hits++;
            System.out.println("L1 Cache HIT (0.5ms)");

            return L1.get(videoId);
        }

        System.out.println("L1 Cache MISS");

        // L2 lookup
        if (L2.containsKey(videoId)) {

            L2_hits++;
            System.out.println("L2 Cache HIT (5ms)");

            VideoData video = L2.get(videoId);

            // promote to L1
            L1.put(videoId, video);

            System.out.println("Promoted to L1");

            return video;
        }

        System.out.println("L2 Cache MISS");

        // L3 database
        if (L3.containsKey(videoId)) {

            L3_hits++;
            System.out.println("L3 Database HIT (150ms)");

            VideoData video = L3.get(videoId);

            // add to L2
            if (L2.size() >= L2_CAPACITY) {

                Iterator<String> it = L2.keySet().iterator();
                it.next();
                it.remove();
            }

            L2.put(videoId, video);

            return video;
        }

        System.out.println("Video not found");
        return null;
    }

    // Cache statistics
    public void getStatistics() {

        int total = L1_hits + L2_hits + L3_hits;

        double L1_rate = (total==0)?0:(L1_hits*100.0/total);
        double L2_rate = (total==0)?0:(L2_hits*100.0/total);
        double L3_rate = (total==0)?0:(L3_hits*100.0/total);

        System.out.println("\nCache Statistics:");

        System.out.println("L1 Hit Rate: " + String.format("%.2f",L1_rate) + "% (0.5ms)");
        System.out.println("L2 Hit Rate: " + String.format("%.2f",L2_rate) + "% (5ms)");
        System.out.println("L3 Hit Rate: " + String.format("%.2f",L3_rate) + "% (150ms)");

        double overall = ((L1_hits+L2_hits)*100.0)/total;

        System.out.println("Overall Cache Hit Rate: " +
                String.format("%.2f",overall) + "%");
    }

    public static void main(String[] args) {

        MultiLevelCache cache = new MultiLevelCache();

        // Populate database
        cache.addVideoToDatabase("video_123","Movie Data");
        cache.addVideoToDatabase("video_456","Sports Data");
        cache.addVideoToDatabase("video_999","News Data");

        System.out.println("Request 1:");
        cache.getVideo("video_123");

        System.out.println("\nRequest 2:");
        cache.getVideo("video_123");

        System.out.println("\nRequest 3:");
        cache.getVideo("video_999");

        cache.getStatistics();
    }
}

