package expression.operations;

import expression.exceptions.DivideByZeroException;

import java.math.BigInteger;

public class BigIntegerOperation implements Operation<BigInteger> {
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
        if (right.equals(BigInteger.ZERO)) {
            throw new DivideByZeroException(left + " / " + right + " - division by zero");
        }
        return left.divide(right);
    }

    @Override
    public BigInteger negative(BigInteger left) {
        return left.negate();
    }
}
