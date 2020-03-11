package expression.operators;

import expression.TripleExpression;
import expression.operations.Operation;

public abstract class UnaryOperator<T> implements TripleExpression<T> {
    private TripleExpression<T> expression;
    protected Operation<T> operation;
    public UnaryOperator (TripleExpression<T> expression, Operation<T> operation) {
        this.expression = expression;
        this.operation = operation;
    }
    public abstract T makeOperation(T val);
    public abstract String getOperator();
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(getOperator());
        stringBuilder.append("(");
        stringBuilder.append(expression.toString());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
    @Override
    public T evaluate(T x, T y, T z) {
        return makeOperation(expression.evaluate(x, y, z));
    }
}
