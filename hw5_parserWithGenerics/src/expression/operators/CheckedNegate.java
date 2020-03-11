package expression.operators;

import expression.TripleExpression;
import expression.calculationType.CalculationType;

public class CheckedNegate<T> extends UnaryOperator<T> {
    public CheckedNegate(TripleExpression<T> exp, CalculationType<T> calculationType) {
        super(exp, calculationType);
    }

    @Override
    public T makeOperation(T val) {
        return calculationType.negative(val);
    }
    @Override
    public String getOperator() {
        return "-";
    }
}
