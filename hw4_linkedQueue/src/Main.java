import queue.LinkedQueue;

public class Main {

    public static void main(String[] args) {
	    LinkedQueue q = new LinkedQueue();
	    q.enqueue(1);
        q.enqueue(2);
        q.enqueue(3);
        q.enqueue(4);
        q.enqueue(5);
        for (int i = 0; i < 5; i++) {
            System.out.println(q.size() + " " + q.dequeue());
        }
    }
}
