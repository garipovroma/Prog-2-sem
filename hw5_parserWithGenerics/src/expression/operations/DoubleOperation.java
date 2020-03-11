package expression.operations;

public class DoubleOperation implements Operation<Double> {
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
}
