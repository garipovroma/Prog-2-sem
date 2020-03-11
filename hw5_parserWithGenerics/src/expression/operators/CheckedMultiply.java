package expression.operators;

import expression.TripleExpression;
import expression.calculationType.CalculationType;

public class CheckedMultiply<T> extends BinaryOperator<T> {
    public CheckedMultiply (TripleExpression<T> left, TripleExpression<T> right, CalculationType<T> calculationType) {
        super(left, right, calculationType);
    }

    @Override
    protected T makeOperation(T left, T right) {
        return calculationType.mul(left, right);
    }

    @Override
    public String getOperator() {
        return "*";
    }
}
