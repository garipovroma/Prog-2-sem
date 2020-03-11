package expression.operators;

import expression.TripleExpression;
import expression.calculationType.CalculationType;

public abstract class UnaryOperator<T> implements TripleExpression<T> {
    private TripleExpression<T> expression;
    protected CalculationType<T> calculationType;
    public UnaryOperator (TripleExpression<T> expression, CalculationType<T> calculationType) {
        this.expression = expression;
        this.calculationType = calculationType;
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
