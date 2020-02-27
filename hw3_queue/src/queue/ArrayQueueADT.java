package queue;

import java.util.Arrays;

public class ArrayQueueADT {
    private Object[] elements = new Object[5];
    private int l = 0, r = 0, size = 0;
    private static void ensureCapacity(ArrayQueueADT queue, int capacity) {
        if (capacity > queue.elements.length) {
            Object[] newElements = new Object[capacity * 2];
            int pos = queue.l;
            for (int i = 0; i < queue.size; i++) {
                if (pos == queue.elements.length) {
                    pos = 0;
                }
                newElements[i] = queue.elements[pos++];
            }
            queue.l = 0;
            queue.r = queue.size;
            queue.elements = newElements;
        }
    }
    // post : q[0]...q[size - 1] - sequence of elements in queue, q' = [q[0], q[1] ... element], size' = size + 1
    public static void enqueue(ArrayQueueADT queue, Object element) {
        assert element != null;
        ensureCapacity(queue,queue.size + 1);
        if (queue.r == queue.elements.length) {
            queue.r = 0;
        }
        queue.elements[queue.r++] = element;
        queue.size++;
    }
    // pred : size > 0
    // post : q[0]...q[size - 1] - sequence of elements in queue, res = q[0]
    public static Object element(ArrayQueueADT queue) {
        assert queue.size > 0;
        if (queue.l == queue.elements.length) {
            queue.l = 0;
        }
        return queue.elements[queue.l];
    }
    // pred : size > 0
    // post : q[0]...q[size - 1] - sequence of elements in queue,
    //           res = q[0], q' = [q[1], q[2] ... q[size - 1]], size' = size - 1
    public static Object dequeue(ArrayQueueADT queue) {
        assert queue.size > 0;
        Object value = element(queue);
        queue.elements[queue.l++] = null;
        queue.size--;
        return value;
    }
    // post : res = size
    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }
    // post : res = true in case size = 0
    public static boolean isEmpty(ArrayQueueADT queue) {
        return (queue.size == 0);
    }
    // post : queue is empty now, size = 0
    public static void clear(ArrayQueueADT queue) {
        queue.l = 0;
        queue.r = 0;
        queue.size = 0;
        for (int i = 0; i < queue.elements.length; i++) {
            queue.elements[i] = null;
        }
    }
}
