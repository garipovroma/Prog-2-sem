package expression.operators;

import expression.TripleExpression;
import expression.exceptions.DivOverflowException;
import expression.exceptions.DivideByZeroException;
import expression.operations.Operation;

public class CheckedDivide<T> extends BinaryOperator<T> {
    public CheckedDivide (TripleExpression<T> left, TripleExpression<T> right, Operation<T> operation) {
        super(left, right, operation);
    }

    @Override
    protected T makeOperation(T left, T right) {
        return operation.div(left, right);
    }

    @Override
    public String getOperator() {
        return "/";
    }

}
