package expression.exceptions;

public class NegateOverflowException extends OverflowException {
    public NegateOverflowException(String string) {
        super(string);
    }
}
