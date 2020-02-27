package queue;

import java.util.Arrays;

public class ArrayQueue {
    private Object[] elements = new Object[5];
    private int l = 0, size = 0;
    private void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            Object[] newElements = new Object[2 * capacity];
            System.arraycopy(elements, l, newElements, 0, elements.length - l);
            System.arraycopy(elements, 0, newElements, elements.length - l, l);
            elements = newElements;
            l = 0;
        }
    }
    public void push(Object element) {
        assert element != null;
        ensureCapacity(size + 1);
        elements[(l + elements.length - 1) % elements.length] = element;
        l = (l + elements.length - 1) % elements.length;
        size++;
    }
    public Object peek() {
        assert size > 0;
        return elements[(l + size - 1) % elements.length];
    }
    public Object remove() {
        assert size > 0;
        Object value = peek();
        elements[(l + size - 1) % elements.length] = null;
        size--;
        return value;
    }
    // post : q[0]...q[size - 1] - sequence of elements in queue, q' = [q[0], q[1] ... element], size' = size + 1
    public void enqueue(Object element) {
        assert element != null;
        ensureCapacity(size + 1);
        elements[(l + size) % elements.length] = element;
        size++;
    }
    // pred : size > 0
    // post : q[0]...q[size - 1] - sequence of elements in queue, res = q[0]
    public Object element() {
        assert size > 0;
        return elements[l];
    }
    // pred : size > 0
    // post : q[0]...q[size - 1] - sequence of elements in queue,
    //           res = q[0], q' = [q[1], q[2] ... q[size - 1]], size' = size - 1
    public Object dequeue() {
        assert size > 0;
        Object value = element();
        elements[l++] = null;
        l %= elements.length;
        size--;
        return value;
    }
    // post : res = size
    public int size() {
        return size;
    }
    // post : res = true in case size = 0
    public boolean isEmpty() {
        return (size == 0);
    }
    // post : queue is empty now, size = 0
    public void clear() {
        l = 0;
        size = 0;
        for (int i = 0; i < elements.length; i++) {
            elements[i] = null;
        }
    }
}
