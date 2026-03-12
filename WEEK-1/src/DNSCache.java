import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    public DNSEntry(String domain, String ipAddress, int ttlSeconds) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + (ttlSeconds * 1000);
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

public class DNSCache {

    private int maxSize;

    private LinkedHashMap<String, DNSEntry> cache;

    private int hits = 0;
    private int misses = 0;

    public DNSCache(int maxSize) {

        this.maxSize = maxSize;

        cache = new LinkedHashMap<String, DNSEntry>(maxSize, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.maxSize;
            }
        };
    }

    // Simulate upstream DNS lookup
    private String queryUpstreamDNS(String domain) {

        System.out.println("Querying upstream DNS for " + domain);

        // Dummy IP generation
        Random rand = new Random();
        return "172.217.14." + rand.nextInt(255);
    }

    public String resolve(String domain) {

        long start = System.nanoTime();

        if (cache.containsKey(domain)) {

            DNSEntry entry = cache.get(domain);

            if (!entry.isExpired()) {
                hits++;

                long end = System.nanoTime();
                System.out.println("Cache HIT → " + entry.ipAddress +
                        " retrieved in " + (end - start) / 1000000.0 + " ms");

                return entry.ipAddress;
            }

            else {
                System.out.println("Cache EXPIRED for " + domain);
                cache.remove(domain);
            }
        }

        misses++;

        String ip = queryUpstreamDNS(domain);

        DNSEntry entry = new DNSEntry(domain, ip, 300);

        cache.put(domain, entry);

        System.out.println("Cache MISS → Stored " + domain + " → " + ip);

        return ip;
    }

    public void getCacheStats() {

        int total = hits + misses;

        double hitRate = (total == 0) ? 0 : (hits * 100.0 / total);

        System.out.println("\nCache Statistics:");
        System.out.println("Hits: " + hits);
        System.out.println("Misses: " + misses);
        System.out.println("Hit Rate: " + hitRate + "%");
    }

    public static void main(String[] args) throws InterruptedException {

        DNSCache dnsCache = new DNSCache(5);

        dnsCache.resolve("google.com");
        dnsCache.resolve("google.com");

        dnsCache.resolve("openai.com");
        dnsCache.resolve("github.com");

        Thread.sleep(2000);

        dnsCache.resolve("google.com");

        dnsCache.getCacheStats();
    }
}