package expression.operators;

import expression.TripleExpression;
import expression.operations.Operation;

public abstract class BinaryOperator<T> implements TripleExpression<T> {
    private TripleExpression<T> left, right;
    protected Operation<T> operation;
    public BinaryOperator(TripleExpression<T> left, TripleExpression<T> right, Operation<T> operation) {
        this.left = left;
        this.right = right;
        this.operation = operation;
    }
    protected abstract T makeOperation(T left, T right);
    protected abstract String getOperator();
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        sb.append(left.toString());
        sb.append(" ");
        sb.append(this.getOperator());
        sb.append(" ");
        sb.append(right.toString());
        sb.append(")");
        return sb.toString();
    }
    @Override
    public T evaluate(T x, T y, T z) {
        return makeOperation(left.evaluate(x, y, z), right.evaluate(x, y, z));
    }
}
