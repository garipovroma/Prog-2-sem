package queue;

import java.lang.reflect.Array;

public class MyArrayQueueTest {
    public static void fill(ArrayQueue queue) {
        for (int i = 0; i < 10; i++) {
            queue.enqueue(i);
        }
    }

    public static void dump(ArrayQueue queue) {
        while (!queue.isEmpty()) {
            System.out.println(queue.size() + " " +
                queue.element() + " " + queue.dequeue());
        }
    }
    public static void clear(ArrayQueue queue) {
        queue.clear();
        System.out.println("Queue cleared : size = " + queue.size());
    }

    public static void main(String[] args) {
        ArrayQueue queue = new ArrayQueue();
        fill(queue);
        dump(queue);
        System.out.println("Queue filled again");
        fill(queue);
        clear(queue);
    }
}
