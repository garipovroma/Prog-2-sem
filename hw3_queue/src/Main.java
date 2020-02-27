import queue.ArrayQueue;

public class Main {

    public static void main(String[] args) {
        ArrayQueue q = new ArrayQueue();
        for (int i = 0; i < 10; i++) {
            q.enqueue(i);
        }
        for (int i = 0; i < 9; i++) {
            System.out.println(q.dequeue());
        }
        System.out.println();
        for (int i = 0; i < 5; i++) {
            q.enqueue(i);
        }
        for (int i = 0; i < 6; i++) {
            System.out.println(q.dequeue());
        }
    }
}
