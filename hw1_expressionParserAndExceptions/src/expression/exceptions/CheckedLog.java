package expression.exceptions;

import expression.BinaryOperator;
import expression.CommonExpression;

public class CheckedLog extends BinaryOperator {
    public CheckedLog (CommonExpression left, CommonExpression right) {
        super(left, right);
    }
    @Override
    public int makeOperation(int left, int right) {
        int x = left;
        int res = 0;
        if (right == 1 || right <= 0 || left <= 0) {
            throw new InvalidLogArgumentsException("Invalid log arguments left = " + left + ", right = " + right);
        }
        while (x >= right) {
            if (super.checkOverflow(x, right)) {
                throw new OverflowException(left + " // " + right + " - overflows");
            }
            x /= right;
            res++;
        }
        return res;
    }
    @Override
    public double makeOperation(double left, double right) {
        return left / right;
    }
    @Override
    public String getOperator() {
        return "//";
    }
    @Override
    public int getPriority() {
        return 2;
    }
}

