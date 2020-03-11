package expression.operators;

import expression.TripleExpression;
import expression.exceptions.DivOverflowException;
import expression.operations.Operation;

public class CheckedMultiply<T> extends BinaryOperator<T> {
    public CheckedMultiply (TripleExpression<T> left, TripleExpression<T> right, Operation<T> operation) {
        super(left, right, operation);
    }

    @Override
    protected T makeOperation(T left, T right) {
        return operation.mul(left, right);
    }

    @Override
    public String getOperator() {
        return "*";
    }
}
