package expression.operators;

import expression.TripleExpression;
import expression.calculationType.CalculationType;

public class CheckedAdd<T> extends BinaryOperator<T> {
    public CheckedAdd (TripleExpression<T> left, TripleExpression<T> right, CalculationType<T> calculationType) {
        super(left, right, calculationType);
    }

    @Override
    protected T makeOperation(T left, T right) {
        return calculationType.add(left, right);
    }

    @Override
    public String getOperator() {
        return "+";
    }
}
