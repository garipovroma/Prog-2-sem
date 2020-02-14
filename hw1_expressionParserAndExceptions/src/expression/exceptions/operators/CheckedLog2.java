package expression.exceptions.operators;

import expression.CommonExpression;
import expression.UnaryOperator;
import expression.exceptions.expressionExceptions.InvalidLogArgumentsException;

public class CheckedLog2 extends UnaryOperator {
    public CheckedLog2(CommonExpression exp) {
        super(exp);
    }

    @Override
    public int makeOperation(int val) {
        if (val <= 0) {
            throw new InvalidLogArgumentsException("Invalid log arguments = " + val);
        }
        int res = 0;
        while (val >= 2) {
            val >>= 1;
            res++;
        }
        return res;
    }
    @Override
    public String getOperator() {
        return "log2";
    }
    public static CommonExpression getLog2(CommonExpression exp) {
        return new CheckedLog2(exp);
    }
}
