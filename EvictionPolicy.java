public interface EvictionPolicy {
    void accessNode(String key);

    Node insertNode(String key, String value);

    Node getNode(String key);

    void display(); // New method to display cache contents
}
