package expression.exceptions.operators;

import expression.BinaryOperator;
import expression.CommonExpression;
import expression.exceptions.expressionExceptions.OverflowException;
import expression.exceptions.expressionExceptions.SubOverflowException;

public class CheckedSubtract extends BinaryOperator {
    public CheckedSubtract (CommonExpression left, CommonExpression right) {
        super(left, right);
    }
    @Override
    public int makeOperation(int left, int right) {
        if (super.checkOverflow(left, right)) {
            throw new SubOverflowException(left + " - " + right + " - overflows");
        }
        return left - right;
    }
    @Override
    public double makeOperation(double left, double right) {
        return left - right;
    }
    @Override
    public String getOperator() {
        return "-";
    }
    @Override
    public int getPriority() {
        return 1;
    }
}
