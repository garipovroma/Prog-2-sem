package expression.exceptions.operators;

import expression.BinaryOperator;
import expression.CommonExpression;
import expression.exceptions.expressionExceptions.DivideByZeroException;
import expression.exceptions.expressionExceptions.InvalidPowArgumentsException;
import expression.exceptions.expressionExceptions.OverflowException;
import expression.exceptions.expressionExceptions.PowOverflowException;

public class CheckedPow extends BinaryOperator {
    public CheckedPow (CommonExpression left, CommonExpression right) {
        super(left, right);
    }
    private boolean overflow = false;
    private int bin_pow(int a, int n) {
        if (n == 0) {
            return 1;
        }
        if (n == 1) {
            return a;
        } else if (n % 2 == 0) {
            int x = bin_pow(a, n / 2);
            if (super.checkOverflow(x, x)) {
                overflow = true;
            }
            return x * x;
        } else {
            int x = bin_pow(a, n - 1);
            if (super.checkOverflow(a, x)) {
                overflow = true;
            }
            return a * x;
        }
    }
    @Override
    public int makeOperation(int left, int right) {
        overflow = false;
        if (left == 0 && right == 0) {
            throw new InvalidPowArgumentsException(left + " ** " + right + " - isn't defined");
        }
        if (left == 0 && right < 0) {
            throw new DivideByZeroException(left + " ** " + right + " - division by zero");
        }
        if (right < 0) {
            throw new InvalidPowArgumentsException(left + " ** " + right + " - isn't defined");
        }
        int x = bin_pow(left, right);
        if (overflow) {
            throw new PowOverflowException(left + " ** " + right + " - overflows");
        }
        return x;
    }
    @Override
    public double makeOperation(double left, double right) {
        return left * right;
    }
    @Override
    public String getOperator() {
        return "**";
    }
    @Override
    public int getPriority() {
        return 2;
    }
}
