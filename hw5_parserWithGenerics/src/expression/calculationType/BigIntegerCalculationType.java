package expression.calculationType;

import expression.exceptions.DivideByZeroException;

import java.math.BigInteger;

public class BigIntegerCalculationType implements CalculationType<BigInteger> {
    private boolean checkOverflow = false;
    public BigIntegerCalculationType(boolean flag) {
        this.checkOverflow = flag;
    }
    @Override
    public BigInteger parse(String string) {
        return new BigInteger(string);
    }

    @Override
    public BigInteger add(BigInteger left, BigInteger right) {
        return left.add(right);
    }

    @Override
    public BigInteger sub(BigInteger left, BigInteger right) {
        return left.subtract(right);
    }

    @Override
    public BigInteger mul(BigInteger left, BigInteger right) {
        return left.multiply(right);
    }

    @Override
    public BigInteger div(BigInteger left, BigInteger right) {
        if (checkOverflow) {
            if (right.equals(BigInteger.ZERO)) {
                throw new DivideByZeroException(left + " / " + right + " - division by zero");
            }
        }
        return left.divide(right);
    }

    @Override
    public BigInteger negative(BigInteger left) {
        return left.negate();
    }

    @Override
    public BigInteger count(BigInteger left) {
        return new BigInteger(Integer.toString(left.bitCount()));
    }

    @Override
    public BigInteger min(BigInteger left, BigInteger right) {
        return left.min(right);
    }

    @Override
    public BigInteger max(BigInteger left, BigInteger right) {
        return left.max(right);
    }
}
