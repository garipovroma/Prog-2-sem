package expression.calculationType;

import expression.exceptions.*;

public class LongCalculationType implements CalculationType<Long> {
    private boolean checkOverflow = false;
    public LongCalculationType(boolean flag) {
        this.checkOverflow = flag;
    }
    @Override
    public Long parse(String string) {
        return Long.parseLong(string);
    }

    @Override
    public Long add(Long left, Long right) {
        if (checkOverflow) {
            if (((right > 0 && Long.MAX_VALUE - right < left) || (right < 0 && Long.MIN_VALUE - right > left))) {
                throw new AddOverflowException(left + " + " + right + " - overflows");
            }
        }
        return (long)((long)left + right);
    }

    @Override
    public Long sub(Long left, Long right) {
        if (checkOverflow) {
            if ((right < 0 && Long.MAX_VALUE + right < left) || (right > 0 && Long.MIN_VALUE + right > left)) {
                throw new SubOverflowException(left + " - " + right + " - overflows");
            }
        }
        return (long)((long)left - right);
    }

    @Override
    public Long mul(Long left, Long right) {
        if (checkOverflow) {
            if (left != 0 && right != 0 && ((left * right) / right != left || (left * right) / left != right)) {
                throw new MulOverflowException(left + " * " + right + " - overflows");
            }
        }
        return (long)((long)left * right);
    }

    @Override
    public Long div(Long left, Long right) {
        if (checkOverflow) {
            if (left == Long.MIN_VALUE && right == -1) {
                throw new DivOverflowException(left + " / " + right + " - overflows");
            }
        }
        if (right == 0) {
            throw new DivideByZeroException(left + " / " + right + " - division by zero");
        }
        return (long)((long)left / right);
    }

    @Override
    public Long negative(Long left) {
        if (checkOverflow) {
            if (left == Long.MIN_VALUE) {
                throw new NegateOverflowException("-" + left + " -  overflows");
            }
        }
        return -left;
    }

    @Override
    public Long count(Long left) {
        return (long)Long.bitCount(left);
    }

    @Override
    public Long min(Long left, Long right) {
        return Long.min(left,right);
    }

    @Override
    public Long max(Long left, Long right) {
        return Long.max(left, right);
    }
}
