package expression.exceptions;

import expression.CommonExpression;
import expression.Const;

public class CheckedPow2 implements CommonExpression {
    private CommonExpression exp;

    public CheckedPow2(CommonExpression exp) {
        this.exp = exp;
    }

    public static CommonExpression getPow2(CommonExpression exp) {
        return new CheckedPow2(exp);
    }

    @Override
    public String toMiniString() {
        StringBuilder stringBuilder = new StringBuilder("pow2(");
        stringBuilder.append(exp.toMiniString());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
    public String toString() {
        return this.toMiniString();
    }

    public int getPow2(int deg) {
        if (deg < 0) {
            throw new InvalidPowArgumentsException("pow2 " + deg + " - invalid pow2 arguments");
        }
        if (deg >= 32) {
            throw new OverflowException("pow2 " + deg + " - overflows");
        }
        return (1 << deg);
    }

    @Override
    public int evaluate(int x) {
        int deg = exp.evaluate(x);
        return getPow2(deg);
    }

    @Override
    public double evaluate(double x) {
        return -exp.evaluate(x);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        int deg = exp.evaluate(x, y, z);
        return getPow2(deg);
    }
}
