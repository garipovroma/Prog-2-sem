package expression.exceptions.operators;

import expression.CommonExpression;
import expression.UnaryOperator;
import expression.exceptions.expressionExceptions.InvalidPowArgumentsException;
import expression.exceptions.expressionExceptions.OverflowException;

public class CheckedPow2 extends UnaryOperator {
    public CheckedPow2(CommonExpression exp) {
        super(exp);
    }

    @Override
    public int makeOperation(int val) {
        if (val < 0) {
            throw new InvalidPowArgumentsException("pow2 " + val + " - invalid pow2 arguments");
        }
        if (val >= 32) {
            throw new OverflowException("pow2 " + val + " - overflows");
        }
        return (1 << val);
    }

    @Override
    public String getOperator() {
        return "pow2";
    }
    public static CommonExpression getPow2(CommonExpression exp) {
        return new CheckedPow2(exp);
    }
}
