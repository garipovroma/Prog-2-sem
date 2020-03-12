package expression.calculationType;

import expression.exceptions.*;

public class IntegerCalculationType implements CalculationType<Integer> {

    @Override
    public Integer parse(String string) {
        return Integer.parseInt(string);
    }

    @Override
    public Integer add(Integer left, Integer right) {
        if (((right > 0 && Integer.MAX_VALUE - right < left) || (right < 0 && Integer.MIN_VALUE - right > left))) {
            throw new AddOverflowException(left + " + " + right + " - overflows");
        }
        return left + right;
    }

    @Override
    public Integer sub(Integer left, Integer right) {
        if ((right < 0 && Integer.MAX_VALUE + right < left) || (right > 0 && Integer.MIN_VALUE + right > left)) {
            throw new SubOverflowException(left + " - " + right + " - overflows");
        }
        return left - right;
    }

    @Override
    public Integer mul(Integer left, Integer right) {
        if (left != 0 && right != 0 && ((left * right) / right != left || (left * right) / left != right)) {
            throw new MulOverflowException(left + " * " + right + " - overflows");
        }
        return left * right;
    }

    @Override
    public Integer div(Integer left, Integer right) {
        if (left == Integer.MIN_VALUE && right == -1) {
            throw new DivOverflowException(left + " / " + right + " - overflows");
        }
        if (right == 0) {
            throw new DivideByZeroException(left + " / " + right + " - division by zero");
        }
        return left / right;
    }

    @Override
    public Integer negative(Integer left) {
        if (left == Integer.MIN_VALUE) {
            throw new NegateOverflowException("-" + left + " -  overflows");
        }
        return -left;
    }
    @Override
    public Integer count(Integer left) {
        return Integer.bitCount(left);
    }

    @Override
    public Integer min(Integer left, Integer right) {
        return Integer.min(left, right);
    }

    @Override
    public Integer max(Integer left, Integer right) {
        return Integer.max(left, right);
    }
}
