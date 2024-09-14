import java.util.HashMap;

public class LRUCacheLevel implements EvictionPolicy {
    private int capacity;
    private HashMap<String, Node> map;
    private DoublyLinkedList list;

    public LRUCacheLevel(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>();
        list = new DoublyLinkedList();
    }

    @Override
    public void accessNode(String key) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            list.removeNode(node);
            list.addNodeToHead(node);
        }
    }

    @Override
    public Node insertNode(String key, String value) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = value;
            list.removeNode(node);
            list.addNodeToHead(node);
            return null; // No eviction in this case
        } else {
            if (map.size() >= capacity) {
                Node tail = list.removeTail();
                map.remove(tail.key);
                return tail; // Return the evicted node
            }
            Node newNode = new Node(key, value);
            list.addNodeToHead(newNode);
            map.put(key, newNode);
            return null;
        }
    }

    @Override
    public Node getNode(String key) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            accessNode(key); // Update LRU
            return node;
        }
        return null;
    }

    @Override
    public void display() {
        Node current = list.head.next;
        while (current != list.tail) {
            System.out.print(current.key + ": " + current.value + " ");
            current = current.next;
        }
        System.out.println();
    }
}
