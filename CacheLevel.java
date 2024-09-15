import java.util.ArrayList;

public class CacheLevel {
    private ArrayList<EvictionPolicy> cacheLevels;

    public CacheLevel() {
        cacheLevels = new ArrayList<>();
    }

    // Add cache level with eviction policy (LRU or LFU)
    public void addCacheLevel(int size, String evictionPolicy) {
        if (evictionPolicy.equalsIgnoreCase("LRU")) {
            cacheLevels.add(new LRUCacheLevel(size));
        } else if (evictionPolicy.equalsIgnoreCase("LFU")) {
            cacheLevels.add(new LFUCacheLevel(size));
        }
    }

    // Remove cache level at specified index
    public void removeCacheLevel(int level) {
        if (level >= 0 && level < cacheLevels.size()) {
            cacheLevels.remove(level);
        }
    }

    // Put value into the cache (starts with L1)
    public void put(String key, String value) {
        // Insert into L1 and handle eviction if necessary
        EvictionPolicy l1Cache = cacheLevels.get(0);
        Node evictedNode = l1Cache.insertNode(key, value); // This should return the evicted node if any

        if (evictedNode != null) {
            // Move evicted node to lower cache levels
            moveToLowerLevels(evictedNode, 1);
        }
    }

    // Helper function to move an evicted node down to lower levels
    private void moveToLowerLevels(Node node, int level) {
        if (level >= cacheLevels.size()) {
            // No more levels to move to, data is lost
            return;
        }

        EvictionPolicy cache = cacheLevels.get(level);
        Node evictedNode = cache.insertNode(node.key, node.value);

        if (evictedNode != null) {
            // If another node is evicted from this lower cache, try to move it further down
            moveToLowerLevels(evictedNode, level + 1);
        }
    }

    // Helper function to promote a node across all higher cache levels
    private void promoteToHigherLevels(String key, String value, int currentLevel) {
        if (currentLevel == 0) {
            // If the item is already at L1, no need to promote further
            return;
        }

        // Promote the item to the next higher level
        EvictionPolicy higherCache = cacheLevels.get(currentLevel - 1);
        Node evictedNode = higherCache.insertNode(key, value);

        if (evictedNode != null) {
            // Move evicted node down to lower levels
            moveToLowerLevels(evictedNode, currentLevel);
        }

        // Recursively promote to the next higher level until reaching L1
        promoteToHigherLevels(key, value, currentLevel - 1);
    }

    // Updated get method to handle promotion across levels
    public String get(String key) {
        for (int i = 0; i < cacheLevels.size(); i++) {
            EvictionPolicy cache = cacheLevels.get(i);
            Node node = cache.getNode(key);
            if (node != null && i != 0) {
                // Promote the node from its current level up to L1 (or next higher levels)
                promoteToHigherLevels(key, node.value, i);
                return node.value;
            }
        }
        return null; // Cache miss
    }

    // Display the contents of the cache levels
    public void displayCache() {
        System.out.println("Total number of levels: " + cacheLevels.size());
        for (int i = 0; i < cacheLevels.size(); i++) {
            System.out.println("Cache Level " + (i + 1) + ":");
            cacheLevels.get(i).display(); // Call display method of each level
        }
    }
}
