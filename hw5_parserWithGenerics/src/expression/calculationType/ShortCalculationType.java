package expression.calculationType;

import expression.exceptions.*;

public class ShortCalculationType implements CalculationType<Short> {
    private boolean checkOverflow = false;
    public ShortCalculationType(boolean flag) {
        this.checkOverflow = flag;
    }
    @Override
    public Short parse(String string) {
        return (short)Integer.parseInt(string);
    }

    @Override
    public Short add(Short left, Short right) {
        if (checkOverflow) {
            if (((right > 0 && Short.MAX_VALUE - right < left) || (right < 0 && Short.MIN_VALUE - right > left))) {
                throw new AddOverflowException(left + " + " + right + " - overflows");
            }
        }
        return (short)((short)left + right);
    }

    @Override
    public Short sub(Short left, Short right) {
        if (checkOverflow) {
            if ((right < 0 && Short.MAX_VALUE + right < left) || (right > 0 && Short.MIN_VALUE + right > left)) {
                throw new SubOverflowException(left + " - " + right + " - overflows");
            }
        }
        return (short)((short)left - right);
    }

    @Override
    public Short mul(Short left, Short right) {
        if (checkOverflow) {
            if (left != 0 && right != 0 && ((left * right) / right != left || (left * right) / left != right)) {
                throw new MulOverflowException(left + " * " + right + " - overflows");
            }
        }
        return (short)((short)left * right);
    }

    @Override
    public Short div(Short left, Short right) {
        if (checkOverflow) {
            if (left == Short.MIN_VALUE && right == -1) {
                throw new DivOverflowException(left + " / " + right + " - overflows");
            }
        }
        if (right == 0) {
            throw new DivideByZeroException(left + " / " + right + " - division by zero");
        }
        return (short)((short)left / right);
    }

    @Override
    public Short negative(Short left) {
        if (checkOverflow) {
            if (left == Short.MIN_VALUE) {
                throw new NegateOverflowException("-" + left + " -  overflows");
            }
        }
        return (short)-left;
    }

    @Override
    public Short count(Short left) {
        return (short)(Integer.bitCount((int)left & 0xffff));
    }

    @Override
    public Short min(Short left, Short right) {
        return (short)Integer.min((short)left, (short)right);
    }

    @Override
    public Short max(Short left, Short right) {
        return (short)Integer.max((short)left, (short)right);
    }
}
