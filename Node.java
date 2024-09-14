public class Node {
    String key;
    String value;
    Node prev, next;
    int frequency;

    public Node(String key, String value) {
        this.key = key;
        this.value = value;
        this.prev = null;
        this.next = null;
        this.frequency = 1; // for LFU
    }
}
