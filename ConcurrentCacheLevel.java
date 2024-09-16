import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ConcurrentCacheLevel {
    private ArrayList<EvictionPolicy> cacheLevels;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public ConcurrentCacheLevel() {
        cacheLevels = new ArrayList<>();
    }

    // Add cache level with eviction policy (LRU or LFU)
    public void addCacheLevel(int size, String evictionPolicy) {
        lock.writeLock().lock(); // Acquire write lock
        try {
            if (evictionPolicy.equalsIgnoreCase("LRU")) {
                cacheLevels.add(new LRUCacheLevel(size));
            } else if (evictionPolicy.equalsIgnoreCase("LFU")) {
                cacheLevels.add(new LFUCacheLevel(size));
            }
            System.out.println("Cache level added: " + evictionPolicy);
        } finally {
            lock.writeLock().unlock(); // Release write lock
        }
    }

    // Remove cache level at specified index
    public void removeCacheLevel(int level) {
        lock.writeLock().lock(); // Acquire write lock
        try {
            if (level >= 0 && level < cacheLevels.size()) {
                cacheLevels.remove(level);
                System.out.println("Cache level " + (level + 1) + " removed.");
            }
        } finally {
            lock.writeLock().unlock(); // Release write lock
        }
    }

    // Put value into the cache (starts with L1)
    public void put(String key, String value) {
        lock.writeLock().lock(); // Acquire write lock
        try {
            EvictionPolicy l1Cache = cacheLevels.get(0);
            Node evictedNode = l1Cache.insertNode(key, value);

            if (evictedNode != null) {
                moveToLowerLevels(evictedNode, 1);
            }
        } finally {
            lock.writeLock().unlock(); // Release write lock
        }
    }

    public String get(String key) {
        lock.readLock().lock(); // Acquire read lock
        try {
            for (int i = 0; i < cacheLevels.size(); i++) {
                EvictionPolicy cache = cacheLevels.get(i);
                Node node = cache.getNode(key);

                if (node != null) {
                    if (i != 0) {
                        // Promote the node from its current level up to L1 (or next higher levels)
                        promoteToHigherLevels(key, node.value, i);
                    }
                    return node.value; // Return the found value immediately
                }
            }
            return null; // Cache miss
        } finally {
            lock.readLock().unlock(); // Release read lock
        }
    }

    // Helper function to move an evicted node down to lower levels
    private void moveToLowerLevels(Node node, int level) {
        if (level >= cacheLevels.size()) {
            return; // No more levels
        }

        EvictionPolicy cache = cacheLevels.get(level);
        Node evictedNode = cache.insertNode(node.key, node.value);

        if (evictedNode != null) {
            moveToLowerLevels(evictedNode, level + 1);
        }
    }

    private void promoteToHigherLevels(String key, String value, int currentLevel) {
        if (currentLevel == 0) {
            // If the item is already at L1, no need to promote further
            return;
        }

        EvictionPolicy higherCache = cacheLevels.get(currentLevel - 1);

        // Insert node into the higher cache level and check for eviction
        Node evictedNode = higherCache.insertNode(key, value);

        if (evictedNode != null) {
            // If another node is evicted from L1, move it down to lower levels
            moveToLowerLevels(evictedNode, currentLevel);
        }

        // No need for further promotion since this method will be recursive
    }

    // Display the contents of the cache levels
    public void displayCache() {
        lock.readLock().lock(); // Acquire read lock
        try {
            System.out.println("Total number of levels: " + cacheLevels.size());
            for (int i = 0; i < cacheLevels.size(); i++) {
                System.out.println("Cache Level " + (i + 1) + ":");
                cacheLevels.get(i).display(); // Call display method of each level
            }
        } finally {
            lock.readLock().unlock(); // Release read lock
        }
    }
}