package queue;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

public class LinkedQueue extends AbstractQueue {
    private Node head = null, tail = null;
    public LinkedQueue() {};
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

    @Override
    public Queue getEmptyQueue() {
        return new LinkedQueue();
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
