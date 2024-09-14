public class Main {
    public static void main(String[] args) {
        CacheLevel cacheSystem = new CacheLevel();

        cacheSystem.addCacheLevel(3, "LRU");
        cacheSystem.addCacheLevel(2, "LFU");

        cacheSystem.put("A", "1");
        cacheSystem.put("B", "2");
        cacheSystem.put("C", "3");
        cacheSystem.get("A");
        cacheSystem.put("D", "4");
        cacheSystem.get("C");

        cacheSystem.displayCache();

        cacheSystem.get("B");
        cacheSystem.displayCache();
    }
}
