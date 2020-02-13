package expression.exceptions;

import expression.CommonExpression;
import expression.Const;

public class CheckedLog2 implements CommonExpression {
    private CommonExpression exp;

    public CheckedLog2(CommonExpression exp) {
        this.exp = exp;
    }

    public static CommonExpression getLog2(CommonExpression exp) {
        return new CheckedLog2(exp);
    }

    @Override
    public String toMiniString() {
        StringBuilder stringBuilder = new StringBuilder("log2(");
        stringBuilder.append(exp.toMiniString());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
    public String toString() {
        return this.toMiniString();
    }

    @Override
    public int evaluate(int x) {
        int y = exp.evaluate(x), res = 0;
        if (y <= 0) {
            throw new InvalidLogArgumentsException("Invalid log arguments = " + y);
        }
        while (y >= 2) {
            y /= 2;
            res++;
        }
        return res;
    }

    @Override
    public double evaluate(double x) {
        return -exp.evaluate(x);
    }

    @Override
    public int evaluate(int x, int y, int z) {
        int k = exp.evaluate(x, y, z), res = 0;
        if (k <= 0) {
            throw new InvalidLogArgumentsException("Invalid log arguments = " + k);
        }
        while (k >= 2) {
            k /= 2;
            res++;
        }
        return res;
    }
}
