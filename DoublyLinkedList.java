public class DoublyLinkedList {
    Node head;
    Node tail;

    public DoublyLinkedList() {
        head = new Node(null, null); // Dummy head node
        tail = new Node(null, null); // Dummy tail node
        head.next = tail;
        tail.prev = head;
    }

    // Adds a node right after the head (most recently used or accessed node)
    public void addNodeToHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    // Removes a specific node from the list
    public void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    // Removes and returns the least recently used (LRU) node, i.e., the node before
    // the tail
    public Node removeTail() {
        if (tail.prev == head) {
            return null; // List is empty
        }
        Node node = tail.prev;
        removeNode(node);
        return node;
    }

    // Check if the list is empty
    public boolean isEmpty() {
        return head.next == tail;
    }
}
