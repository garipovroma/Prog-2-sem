package expression.operators;

import expression.TripleExpression;
import expression.calculationType.CalculationType;

public class Max<T> extends BinaryOperator<T> {
    public Max (TripleExpression<T> left, TripleExpression<T> right, CalculationType<T> calculationType) {
        super(left, right, calculationType);
    }
    @Override
    protected T makeOperation(T left, T right) {
        return calculationType.max(left, right);
    }
    @Override
    protected String getOperator() {
        return "min";
    }
}
