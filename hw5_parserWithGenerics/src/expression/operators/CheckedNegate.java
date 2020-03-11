package expression.operators;

import expression.TripleExpression;
import expression.exceptions.NegateOverflowException;
import expression.operations.Operation;

public class CheckedNegate<T> extends UnaryOperator<T> {
    public CheckedNegate(TripleExpression<T> exp, Operation<T> operation) {
        super(exp, operation);
    }

    @Override
    public T makeOperation(T val) {
        return operation.negative(val);
    }
    @Override
    public String getOperator() {
        return "-";
    }
}
