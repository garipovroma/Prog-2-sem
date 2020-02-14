package expression;

import expression.exceptions.StringSource;

public abstract class UnaryOperator implements CommonExpression {
    private CommonExpression expression;
    public UnaryOperator (CommonExpression expression) {
        this.expression = expression;
    }
    public abstract int makeOperation(int val);
    public abstract String getOperator();
    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder(getOperator());
        stringBuilder.append("(");
        stringBuilder.append(expression.toString());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
    @Override
    public String toMiniString() {
        StringBuilder stringBuilder = new StringBuilder(getOperator());
        stringBuilder.append("(");
        stringBuilder.append(expression.toString());
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
    @Override
    public int evaluate(int x) {
        int val = expression.evaluate(x);
        return makeOperation(val);
    }
    @Override
    public int evaluate(int x, int y, int z) {
        int val = expression.evaluate(x, y, z);
        return makeOperation(val);

    }
    @Override
    public double evaluate(double x) {
        return 0;
    }
}
