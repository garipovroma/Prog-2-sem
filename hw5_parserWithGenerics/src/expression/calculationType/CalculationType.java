package expression.calculationType;

public interface CalculationType<T> {
    public T parse(String string);
    public T add(T left, T right);
    public T sub(T left, T right);
    public T mul(T left, T right);
    public T div(T left, T right);
    public T negative(T left);
    public T count(T left);
    public T min(T left, T right);
    public T max(T left, T right);
}
