package queue;

public class LinkedQueue extends AbstractQueue {
    private Node head = null, tail = null;
    @Override
    public void enqueue(Object element) {
        size++;
        if (head == null) {
            head = new Node(element);
            tail = head;
            return;
        }
        Node newNode = new Node(element);
        tail.setNext(newNode);
        tail = newNode;
    }

    @Override
    public Object element() {
        assert head != null;
        return head.getValue();
    }

    @Override
    public Object dequeue() {
        assert head != null;
        Object value = head.getValue();
        head = head.getNext();
        size--;
        return value;
    }
    @Override
    public void clear() {
        head = null;
        size = 0;
    }
    private class Node {
        private Object value;
        private Node next = null;
        public Node(Object value) {
            this.value = value;
        }
        private Object getValue() {
            return this.value;
        }
        private Node getNext() {
            return this.next;
        }
        private void setNext(Node newNext) {
            this.next = newNext;
        }
    }
}
