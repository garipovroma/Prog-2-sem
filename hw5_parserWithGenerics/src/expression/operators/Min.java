package expression.operators;

import expression.TripleExpression;
import expression.calculationType.CalculationType;

public class Min<T> extends BinaryOperator<T> {
    public Min (TripleExpression<T> left, TripleExpression<T> right, CalculationType<T> calculationType) {
        super(left, right, calculationType);
    }
    @Override
    protected T makeOperation(T left, T right) {
        return calculationType.min(left, right);
    }
    @Override
    protected String getOperator() {
        return "min";
    }
}
