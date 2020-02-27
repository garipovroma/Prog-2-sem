package queue;

public class MyArrayQueueModuleTest {
    public static void fill() {
        for (int i = 0; i < 10; i++) {
            ArrayQueueModule.enqueue(i);
        }
    }

    public static void dump() {
        while (!ArrayQueueModule.isEmpty()) {
            System.out.println(
                ArrayQueueModule.size() + " " +
                ArrayQueueModule.element() + " " +
                ArrayQueueModule.dequeue());
        }
    }

    public static void clear() {
        ArrayQueueModule.clear();
        System.out.println("Queue cleared : size = " + ArrayQueueModule.size());
    }

    public static void main(String[] args) {
        fill();
        dump();
        System.out.println("Queue filled again");
        fill();
        clear();
    }
}
