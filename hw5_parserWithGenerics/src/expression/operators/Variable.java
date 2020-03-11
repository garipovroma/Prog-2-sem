package expression.operators;

import expression.TripleExpression;

public class Variable<T> implements TripleExpression<T> {
    private String name;
    public Variable(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return name;
    }
    @Override
    public boolean equals(Object exp) {
        if (exp instanceof Variable) {
            return  (this.name.equals(((Variable) exp).name));
        } else {
            return false;
        }
    }
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public T evaluate(T x, T y, T z) {
        switch (name) {
            case "x":
                return x;
            case "y":
                return y;
        }
        return z;
    }
}
