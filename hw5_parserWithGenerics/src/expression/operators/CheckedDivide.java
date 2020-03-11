package expression.operators;

import expression.TripleExpression;
import expression.calculationType.CalculationType;

public class CheckedDivide<T> extends BinaryOperator<T> {
    public CheckedDivide (TripleExpression<T> left, TripleExpression<T> right, CalculationType<T> calculationType) {
        super(left, right, calculationType);
    }

    @Override
    protected T makeOperation(T left, T right) {
        return calculationType.div(left, right);
    }

    @Override
    public String getOperator() {
        return "/";
    }

}
