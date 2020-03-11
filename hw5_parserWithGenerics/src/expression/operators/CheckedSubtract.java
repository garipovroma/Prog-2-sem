package expression.operators;

import expression.TripleExpression;
import expression.exceptions.SubOverflowException;
import expression.operations.Operation;

public class CheckedSubtract<T> extends BinaryOperator<T> {
    public CheckedSubtract (TripleExpression<T> left, TripleExpression<T> right, Operation<T> operation) {
        super(left, right, operation);
    }

    @Override
    protected T makeOperation(T left, T right) {
        return operation.sub(left, right);
    }

    @Override
    public String getOperator() {
        return "-";
    }
}
