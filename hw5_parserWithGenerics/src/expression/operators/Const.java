package expression.operators;

import expression.TripleExpression;

import java.util.Objects;

public class Const<T> implements TripleExpression<T> {
    private T value;
    public Const (T value) {
        this.value = value;
    }
    @Override
    public String toString() {
        return value.toString();
    }
    @Override
    public T evaluate(T x, T y, T z) {
        return value;
    }
    @Override
    public boolean equals(Object exp) {
        if (exp instanceof Const) {
            return  (this.value == ((Const) exp).value);
        } else {
            return false;
        }
    }
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
