import java.util.*;

public class FlashSaleInventoryManager {

    private HashMap<String, Integer> stockMap;

    private HashMap<String, Queue<Integer>> waitingList;

    public FlashSaleInventoryManager() {
        stockMap = new HashMap<>();
        waitingList = new HashMap<>();
    }

    public void addProduct(String productId, int stock) {
        stockMap.put(productId, stock);
        waitingList.put(productId, new LinkedList<>());
    }

    public int checkStock(String productId) {
        return stockMap.getOrDefault(productId, 0);
    }

    public synchronized String purchaseItem(String productId, int userId) {

        int stock = stockMap.getOrDefault(productId, 0);

        if (stock > 0) {
            stockMap.put(productId, stock - 1);
            return "Success: User " + userId +
                    " purchased " + productId +
                    ". Remaining stock: " + (stock - 1);
        } else {
            Queue<Integer> queue = waitingList.get(productId);
            queue.add(userId);
            return "Stock unavailable. User " + userId +
                    " added to waiting list at position #" + queue.size();
        }
    }


    public Queue<Integer> getWaitingList(String productId) {
        return waitingList.get(productId);
    }

    public static void main(String[] args) {

        FlashSaleInventoryManager manager = new FlashSaleInventoryManager();


        manager.addProduct("IPHONE15_256GB", 100);

        System.out.println("Stock available: " +
                manager.checkStock("IPHONE15_256GB"));

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(manager.purchaseItem("IPHONE15_256GB", 67890));

        for (int i = 0; i < 100; i++) {
            manager.purchaseItem("IPHONE15_256GB", i);
        }

        System.out.println(manager.purchaseItem("IPHONE15_256GB", 99999));

        System.out.println("Waiting list: " +
                manager.getWaitingList("IPHONE15_256GB"));
    }
}

