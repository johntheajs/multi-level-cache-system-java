import java.util.HashMap;

public class LFUCacheLevel implements EvictionPolicy {
    private int capacity;
    private HashMap<String, Node> map; // Key to Node mapping
    private HashMap<Integer, DoublyLinkedList> frequencyMap; // Frequency to Node List mapping
    private int minFrequency; // Track the minimum frequency

    public LFUCacheLevel(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.frequencyMap = new HashMap<>();
        this.minFrequency = 1; // Start with frequency 1
    }

    // Access a node (get) and update its frequency
    @Override
    public void accessNode(String key) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            incrementFrequency(node); // Move the node to the appropriate frequency list
        }
    }

    // Insert a node into the cache
    @Override
    public Node insertNode(String key, String value) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            node.value = value; // Update the value
            incrementFrequency(node); // Update its frequency
            return null; // No eviction
        } else {
            if (map.size() >= capacity) {
                // Evict the least frequently used node
                Node nodeToEvict = evictLFUNode();
                map.remove(nodeToEvict.key);
                return nodeToEvict; // Return the evicted node
            }
            // Insert new node with frequency 1
            Node newNode = new Node(key, value);
            newNode.frequency = 1;
            addNodeToFrequencyList(newNode);
            map.put(key, newNode); // Add to the map
            return null; // No eviction in this case
        }
    }

    // Increment frequency of a node and move it to the correct frequency list
    private void incrementFrequency(Node node) {
        int currentFreq = node.frequency;
        // Remove the node from its current frequency list
        frequencyMap.get(currentFreq).removeNode(node);

        // If the current frequency list becomes empty, remove it from the frequency map
        if (frequencyMap.get(currentFreq).isEmpty()) {
            frequencyMap.remove(currentFreq);
            // If this was the minimum frequency, increment minFrequency
            if (currentFreq == minFrequency) {
                minFrequency++;
            }
        }

        // Increment the node's frequency and move it to the next frequency list
        node.frequency++;
        addNodeToFrequencyList(node);
    }

    // Add a node to the appropriate frequency list
    private void addNodeToFrequencyList(Node node) {
        int freq = node.frequency;
        frequencyMap.putIfAbsent(freq, new DoublyLinkedList());
        frequencyMap.get(freq).addNodeToHead(node);

        // Update the minimum frequency
        if (freq < minFrequency) {
            minFrequency = freq;
        }
    }

    // Evict the least frequently used node (LFU logic)
    private Node evictLFUNode() {
        // Get the list of nodes with the minimum frequency
        DoublyLinkedList nodesWithMinFreq = frequencyMap.get(minFrequency);
        // Remove the least recently used node (tail of the list for that frequency)
        Node nodeToEvict = nodesWithMinFreq.removeTail();

        // If the list for this frequency is now empty, remove it from the map
        if (nodesWithMinFreq.isEmpty()) {
            frequencyMap.remove(minFrequency);
        }
        return nodeToEvict;
    }

    @Override
    public Node getNode(String key) {
        if (map.containsKey(key)) {
            Node node = map.get(key);
            accessNode(key); // Update LFU
            return node;
        }
        return null;
    }

    @Override
    public void display() {
        System.out.print("Cache: ");
        for (Integer freq : frequencyMap.keySet()) {
            DoublyLinkedList list = frequencyMap.get(freq);
            Node current = list.head.next;
            while (current != list.tail) {
                System.out.print(current.key + ": " + current.value + " (Freq: " + current.frequency + ") ");
                current = current.next;
            }
        }
        System.out.println();
    }
}
