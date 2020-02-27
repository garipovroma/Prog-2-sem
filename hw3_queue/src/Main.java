import queue.ArrayQueue;

public class Main {

    public static void main(String[] args) {
        ArrayQueue q = new ArrayQueue();
        for (int i = 0; i < 10; i++) {
            q.push(i);
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(q.remove());
        }
    }
}
