package queue;

import java.util.Arrays;

public class ArrayQueueADT {
    // INV of queue : we can add elements to queue end , delete them from end of queue,
    // get element from begin of queue
    // i-th added element will be i-th deleted element
    // suppose that (size) = queue size, q[0] - begin of queue, q[size - 1] - end of queue
    // for all i in 0 .. size - 1 : q[i] != null
    private Object[] elements = new Object[5];
    private int l = 0, size = 0;
    // pred : queue != null
    private static void ensureCapacity(ArrayQueueADT queue, int capacity) {
        if (capacity > queue.elements.length) {
            Object[] newElements = new Object[2 * capacity];
            System.arraycopy(queue.elements, queue.l, newElements, 0, queue.elements.length - queue.l);
            System.arraycopy(queue.elements, 0, newElements, queue.elements.length - queue.l, queue.l);
            queue.elements = newElements;
            queue.l = 0;
        }
    }
    // pred : element != null
    // post : size' = size + 1, for all i in 0..size - 1 : q[i] = q'[i + 1] && q[0]' == element
    public static void push(ArrayQueueADT queue, Object element) {
        assert element != null;
        ensureCapacity(queue,queue.size + 1);
        queue.elements[(queue.l + queue.elements.length - 1) % queue.elements.length] = element;
        queue.l = (queue.l + queue.elements.length - 1) % queue.elements.length;
        queue.size++;
    }
    // pred : size > 0
    // post : res = q[0], immutable
    public static Object peek(ArrayQueueADT queue) {
        assert queue.size > 0;
        return queue.elements[(queue.l + queue.size - 1) % queue.elements.length];
    }
    // pred : size > 0
    // post: res = q[0] && size' = size - 1 && for all i=0..size - 2 : q'[i] = q[i + 1]
    public static Object remove(ArrayQueueADT queue) {
        assert queue.size > 0;
        Object value = peek(queue);
        queue.elements[(queue.l + queue.size - 1) % queue.elements.length] = null;
        queue.size--;
        return value;
    }

    // pred : element != null
    // post : size' = size + 1, for all i in 0..size - 1 : q[i] = q[i]' && q[size' - 1] = element
    public static void enqueue(ArrayQueueADT queue, Object element) {
        assert element != null;
        ensureCapacity(queue, queue.size + 1);
        queue.elements[(queue.l + queue.size) % queue.elements.length] = element;
        queue.size++;
    }
    // pred : size > 0
    // post : res = q[size - 1], immutable
    public static Object element(ArrayQueueADT queue) {
        assert queue.size > 0;
        return queue.elements[queue.l];
    }
    // pred : size > 0
    // post: res = q[size - 1] && size' = size - 1 && for all i=1..size - 1 : q[i - 1]' = q[i]
    public static Object dequeue(ArrayQueueADT queue) {
        assert queue.size > 0;
        Object value = element(queue);
        queue.elements[queue.l++] = null;
        queue.l %= queue.elements.length;
        queue.size--;
        return value;
    }
    // post : res = size, immutable
    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }
    // post : res = true in case size = 0, immutable
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
