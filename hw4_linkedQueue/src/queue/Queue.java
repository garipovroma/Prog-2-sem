package queue;

public interface Queue {
    // INV of queue : we can add elements to queue end , delete them from end of queue,
    // get element from begin of queue
    // i-th added element will be i-th deleted element
    // suppose that (size) = queue size, q - sequence of queue elements,
    // q[0] - begin of queue, q[size - 1] - end of queue
    // for all i in 0 .. size - 1 : q[i] != null

    // pred : size > 0
    // post : res = q[size - 1], immutable
    public Object element();

    // pred : element != null
    // post : size' = size + 1, for all i in 0..size - 1 : q[i] = q[i]' && q[size' - 1] = element
    public void enqueue(Object element);

    // pred : size > 0
    // post: res = q[size - 1] && size' = size - 1 && for all i=1..size - 1 : q[i - 1]' = q[i]
    public Object dequeue();

    // post : res = size, immutable
    public int size();

    // post : res = true in case size = 0, immutable
    public boolean isEmpty();

    // post : queue is empty now, size = 0
    public void clear();
}
