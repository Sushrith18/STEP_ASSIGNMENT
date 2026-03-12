import java.util.HashMap;

class TokenBucket {

    int tokens;
    int maxTokens;
    double refillRate; // tokens per second
    long lastRefillTime;

    public TokenBucket(int maxTokens, double refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
        this.tokens = maxTokens;
        this.lastRefillTime = System.currentTimeMillis();
    }

    // refill tokens based on elapsed time
    private void refill() {

        long now = System.currentTimeMillis();
        double elapsedSeconds = (now - lastRefillTime) / 1000.0;

        int tokensToAdd = (int) (elapsedSeconds * refillRate);

        if (tokensToAdd > 0) {
            tokens = Math.min(maxTokens, tokens + tokensToAdd);
            lastRefillTime = now;
        }
    }

    public synchronized boolean allowRequest() {

        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }

    public int getRemainingTokens() {
        return tokens;
    }
}

public class RateLimiter {

    // clientId -> token bucket
    private HashMap<String, TokenBucket> clients = new HashMap<>();

    private final int MAX_REQUESTS = 1000;
    private final double REFILL_RATE = 1000.0 / 3600; // 1000 per hour

    public boolean checkRateLimit(String clientId) {

        clients.putIfAbsent(clientId,
                new TokenBucket(MAX_REQUESTS, REFILL_RATE));

        TokenBucket bucket = clients.get(clientId);

        boolean allowed = bucket.allowRequest();

        if (allowed) {
            System.out.println("Allowed (" +
                    bucket.getRemainingTokens() +
                    " requests remaining)");
        } else {
            System.out.println("Denied (Rate limit exceeded)");
        }

        return allowed;
    }

    public void getRateLimitStatus(String clientId) {

        TokenBucket bucket = clients.get(clientId);

        if (bucket == null) {
            System.out.println("Client not found");
            return;
        }

        int used = MAX_REQUESTS - bucket.getRemainingTokens();

        System.out.println("Status → used: " + used +
                ", limit: " + MAX_REQUESTS);
    }

    public static void main(String[] args) {

        RateLimiter limiter = new RateLimiter();

        String client = "abc123";

        for (int i = 0; i < 5; i++) {
            limiter.checkRateLimit(client);
        }

        limiter.getRateLimitStatus(client);
    }
}

