package queue;

import java.util.Arrays;

public class ArrayQueue extends AbstractQueue {
    private int l = 0;
    private Object[] elements = new Object[5];
    public ArrayQueue() {};
    private void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            Object[] newElements = new Object[2 * capacity];
            System.arraycopy(elements, l, newElements, 0, elements.length - l);
            System.arraycopy(elements, 0, newElements, elements.length - l, l);
            elements = newElements;
            l = 0;
        }
    }
    @Override
    public void enqueue(Object element) {
        assert element != null;
        ensureCapacity(size + 1);
        elements[(l + size) % elements.length] = element;
        size++;
    }
    @Override
    public Object element() {
        assert size > 0;
        return elements[l];
    }
    @Override
    public Object dequeue() {
        assert size > 0;
        Object value = element();
        elements[l++] = null;
        l %= elements.length;
        size--;
        return value;
    }
    @Override
    public void clear() {
        l = 0;
        size = 0;
        for (int i = 0; i < elements.length; i++) {
            elements[i] = null;
        }
    }

    @Override
    public Queue getEmptyQueue() {
        return new ArrayQueue();
    }
}
