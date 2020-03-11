package queue;

import java.util.Arrays;

public class ArrayQueueModule {
    // INV of queue : we can add elements to queue end , delete them from end of queue,
    // get element from begin of queue
    // i-th added element will be i-th deleted element
    // suppose that (size) = queue size, q[0] - begin of queue, q[size - 1] - end of queue
    // for all i in 0 .. size - 1 : q[i] != null
    private static Object[] elements = new Object[5];
    private static int l = 0, size = 0;
    private static void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            Object[] newElements = new Object[2 * capacity];
            System.arraycopy(elements, l, newElements, 0, elements.length - l);
            System.arraycopy(elements, 0, newElements, elements.length - l, l);
            elements = newElements;
            l = 0;
        }
    }
    // pred : element != null
    // post : size' = size + 1, for all i in 0..size - 1 : q[i] = q'[i + 1] && q[0]' == element
    public static void push(Object element) {
        assert element != null;
        ensureCapacity(size + 1);
        elements[(l + elements.length - 1) % elements.length] = element;
        l = (l + elements.length - 1) % elements.length;
        size++;
    }
    // pred : size > 0
    // post : res = q[0], immutable
    public static Object peek() {
        assert size > 0;
        return elements[(l + size - 1) % elements.length];
    }
    // pred : size > 0
    // post: res = q[0] && size' = size - 1 && for all i=0..size - 2 : q'[i] = q[i + 1]
    public static Object remove() {
        assert size > 0;
        Object value = peek();
        elements[(l + size - 1) % elements.length] = null;
        size--;
        return value;
    }

    // pred : element != null
    // post : size' = size + 1, for all i in 0..size - 1 : q[i] = q[i]' && q[size' - 1] = element
    public static void enqueue(Object element) {
        assert element != null;
        ensureCapacity(size + 1);
        elements[(l + size) % elements.length] = element;
        size++;
    }
    // pred : size > 0
    // post : res = q[size - 1], immutable
    public static Object element() {
        assert size > 0;
        return elements[l];
    }
    // pred : size > 0
    // post: res = q[size - 1] && size' = size - 1 && for all i=1..size - 1 : q[i - 1]' = q[i]
    public static Object dequeue() {
        assert size > 0;
        Object value = element();
        elements[l++] = null;
        l %= elements.length;
        size--;
        return value;
    }
    // post : res = size, immutable
    public static int size() {
        return size;
    }
    // post : res = true in case size = 0, immutable
    public static boolean isEmpty() {
        return (size == 0);
    }
    // post : queue is empty now, size = 0
    public static void clear() {
        l = 0;
        size = 0;
        for (int i = 0; i < elements.length; i++) {
            elements[i] = null;
        }
    }
}
