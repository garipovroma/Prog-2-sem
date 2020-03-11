package expression.operators;

import expression.TripleExpression;
import expression.operations.Operation;

public class CheckedAdd<T> extends BinaryOperator<T> {
    public CheckedAdd (TripleExpression<T> left, TripleExpression<T> right, Operation<T> operation) {
        super(left, right, operation);
    }

    @Override
    protected T makeOperation(T left, T right) {
        return operation.add(left, right);
    }

    @Override
    public String getOperator() {
        return "+";
    }
}
