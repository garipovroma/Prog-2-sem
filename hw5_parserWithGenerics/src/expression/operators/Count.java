package expression.operators;

import expression.TripleExpression;
import expression.calculationType.CalculationType;

public class Count<T> extends UnaryOperator<T> {
    public Count(TripleExpression<T> left, CalculationType<T> calculationType) {
        super(left, calculationType);
    }
    @Override
    public T makeOperation(T val) {
        return calculationType.count(val);
    }

    @Override
    public String getOperator() {
        return "count";
    }
}
