package expression.exceptions;

public class OverflowException extends EvaluatingException {
    public OverflowException (final String string) {
        super(string);
    }
}
