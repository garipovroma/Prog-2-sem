package queue;

import java.util.Arrays;

public class ArrayQueueADT {
    private Object[] elements = new Object[5];
    private int l = 0, size = 0;
    private static void ensureCapacity(ArrayQueueADT queue, int capacity) {
        if (capacity > queue.elements.length) {
            Object[] newElements = new Object[2 * capacity];
            System.arraycopy(queue.elements, queue.l, newElements, 0, queue.elements.length - queue.l);
            System.arraycopy(queue.elements, 0, newElements, queue.elements.length - queue.l, queue.l);
            queue.elements = newElements;
            queue.l = 0;
        }
    }
    public static void push(ArrayQueueADT queue, Object element) {
        assert element != null;
        ensureCapacity(queue,queue.size + 1);
        queue.elements[(queue.l + queue.elements.length - 1) % queue.elements.length] = element;
        queue.l = (queue.l + queue.elements.length - 1) % queue.elements.length;
        queue.size++;
    }
    public static Object peek(ArrayQueueADT queue) {
        assert queue.size > 0;
        return queue.elements[(queue.l + queue.size - 1) % queue.elements.length];
    }
    public static Object remove(ArrayQueueADT queue) {
        assert queue.size > 0;
        Object value = peek(queue);
        queue.elements[(queue.l + queue.size - 1) % queue.elements.length] = null;
        queue.size--;
        return value;
    }

    // post : q[0]...q[size - 1] - sequence of elements in queue, q' = [q[0], q[1] ... element], size' = size + 1
    public static void enqueue(ArrayQueueADT queue, Object element) {
        assert element != null;
        ensureCapacity(queue, queue.size + 1);
        queue.elements[(queue.l + queue.size) % queue.elements.length] = element;
        queue.size++;
    }
    // pred : size > 0
    // post : q[0]...q[size - 1] - sequence of elements in queue, res = q[0]
    public static Object element(ArrayQueueADT queue) {
        assert queue.size > 0;
        return queue.elements[queue.l];
    }
    // pred : size > 0
    // post : q[0]...q[size - 1] - sequence of elements in queue,
    //           res = q[0], q' = [q[1], q[2] ... q[size - 1]], size' = size - 1
    public static Object dequeue(ArrayQueueADT queue) {
        assert queue.size > 0;
        Object value = element(queue);
        queue.elements[queue.l++] = null;
        queue.l %= queue.elements.length;
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
        queue.size = 0;
        for (int i = 0; i < queue.elements.length; i++) {
            queue.elements[i] = null;
        }
    }
}
