package queue;

import java.util.Arrays;

public class ArrayQueueModule {
    private static Object[] elements = new Object[5];
    private static int l = 0, r = 0, size = 0;
    private static void ensureCapacity(int capacity) {
        if (capacity > elements.length) {
            Object[] newElements = new Object[capacity * 2];
            int pos = l;
            for (int i = 0; i < size; i++) {
                if (pos == elements.length) {
                    pos = 0;
                }
                newElements[i] = elements[pos++];
            }
            l = 0;
            r = size;
            elements = newElements;
        }
    }
    // post : q[0]...q[size - 1] - sequence of elements in queue, q' = [q[0], q[1] ... element], size' = size + 1
    public static void enqueue(Object element) {
        assert element != null;
        ensureCapacity(size + 1);
        if (r == elements.length) {
            r = 0;
        }
        elements[r++] = element;
        size++;
    }
    // pred : size > 0
    // post : q[0]...q[size - 1] - sequence of elements in queue, res = q[0]
    public static Object element() {
        assert size > 0;
        if (l == elements.length) {
            l = 0;
        }
        return elements[l];
    }
    // pred : size > 0
    // post : q[0]...q[size - 1] - sequence of elements in queue,
    //           res = q[0], q' = [q[1], q[2] ... q[size - 1]], size' = size - 1
    public static Object dequeue() {
        assert size > 0;
        Object value = element();
        elements[l++] = null;
        size--;
        return value;
    }
    // post : res = size
    public static int size() {
        return size;
    }
    // post : res = true in case size = 0
    public static boolean isEmpty() {
        return (size == 0);
    }
    // post : queue is empty now, size = 0
    public static void clear() {
        l = 0;
        r = 0;
        size = 0;
        for (int i = 0; i < elements.length; i++) {
            elements[i] = null;
        }
    }
}
