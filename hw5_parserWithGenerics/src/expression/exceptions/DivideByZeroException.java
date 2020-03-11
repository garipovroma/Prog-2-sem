package expression.exceptions;

public class DivideByZeroException extends EvaluatingException {
    public DivideByZeroException (final String string) {
        super(string);
    }
}
