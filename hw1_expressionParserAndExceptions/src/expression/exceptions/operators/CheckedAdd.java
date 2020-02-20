package expression.exceptions.operators;

import expression.BinaryOperator;
import expression.CommonExpression;
import expression.exceptions.expressionExceptions.AddOverflowException;

public class CheckedAdd extends BinaryOperator {
    public CheckedAdd (CommonExpression left, CommonExpression right) {
        super(left, right);
    }
    @Override
    public int makeOperation(int left, int right) {
        if (super.checkOverflow(left, right)) {
            throw new AddOverflowException(left + " + " + right + " - overflows");
        }
        return left + right;
    }
    @Override
    public double makeOperation(double left, double right) {
        return left + right;
    }
    @Override
    public String getOperator() {
        return "+";
    }
    @Override
    public int getPriority() {
        return 1;
    }
}
