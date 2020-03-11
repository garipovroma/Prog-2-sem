package queue;

import java.util.function.Function;
import java.util.function.Predicate;

public abstract class AbstractQueue implements Queue {
    protected int size = 0;
    @Override
    public int size() {
        return size;
    }
    @Override
    public boolean isEmpty() {
        return (size == 0);
    }
    @Override
    public Queue filter(Predicate predicate) {
        Queue queue = getEmptyQueue();
        for (int i = 0; i < size; i++) {
            Object value = this.dequeue();
            if (predicate.test(value)) {
                queue.enqueue(value);
            }
            this.enqueue(value);
        }
        return queue;
    }
    @Override
    public Queue map(Function function) {
        Queue queue = getEmptyQueue();
        for (int i = 0; i < size; i++) {
            Object value = this.dequeue();
            queue.enqueue(function.apply(value));
            this.enqueue(value);
        }
        return queue;
    }

    // post : res = new queue, which size == 0
    public abstract Queue getEmptyQueue();
}
