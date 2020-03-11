package expression.operations;

public interface Operation<T> {
    public T parse(String string);
    public T add(T left, T right);
    public T sub(T left, T right);
    public T mul(T left, T right);
    public T div(T left, T right);
    public T negative(T left);
}
