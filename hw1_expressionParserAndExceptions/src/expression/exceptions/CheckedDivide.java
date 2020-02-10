package expression.exceptions;

import expression.AbstractOperator;
import expression.CommonExpression;

public class CheckedDivide extends AbstractOperator {
    public CheckedDivide (CommonExpression left, CommonExpression right) {
        super(left, right);
    }
    @Override
    public int makeOperation(int left, int right) {
        return left / right;
    }
    @Override
    public double makeOperation(double left, double right) {
        return left / right;
    }
    @Override
    public String getOperator() {
        return "/";
    }
    @Override
    public int getPriority() {
        return 2;
    }
}
