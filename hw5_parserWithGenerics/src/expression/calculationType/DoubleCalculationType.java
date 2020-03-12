package expression.calculationType;

public class DoubleCalculationType implements CalculationType<Double> {
    private boolean checkOverflow = false;
    public DoubleCalculationType(boolean flag) {
        this.checkOverflow = flag;
    }
    @Override
    public Double parse(String string) {
        return Double.parseDouble(string);
    }

    @Override
    public Double add(Double left, Double right) {
        return left + right;
    }

    @Override
    public Double sub(Double left, Double right) {
        return left - right;
    }

    @Override
    public Double mul(Double left, Double right) {
        return left * right;
    }

    @Override
    public Double div(Double left, Double right) {
        return left / right;
    }

    @Override
    public Double negative(Double left) {
        return -left;
    }

    @Override
    public Double count(Double left) {
        return (double) Long.bitCount(Double.doubleToLongBits(left));
    }

    @Override
    public Double min(Double left, Double right) {
        return Double.min(left, right);
    }

    @Override
    public Double max(Double left, Double right) {
        return Double.max(left, right);
    }
}
