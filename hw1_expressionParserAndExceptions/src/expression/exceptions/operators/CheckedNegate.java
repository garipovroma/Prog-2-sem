package expression.exceptions.operators;

import expression.CommonExpression;
import expression.Const;
import expression.UnaryOperator;
import expression.exceptions.expressionExceptions.NegateOverflowException;
import expression.exceptions.expressionExceptions.OverflowException;

public class CheckedNegate extends UnaryOperator {
    public CheckedNegate(CommonExpression exp) {
        super(exp);
    }

    @Override
    public int makeOperation(int val) {
        if (val == Integer.MIN_VALUE) {
            throw new NegateOverflowException("-(" + val + ") - overflows");
        }
        return -val;
    }

    @Override
    public String getOperator() {
        return "-";
    }

    public static CommonExpression getNegative(CommonExpression exp) {
        if (exp instanceof Const) {
            return new Const(-exp.evaluate(0));
        }
        return new CheckedNegate(exp);
    }
}
