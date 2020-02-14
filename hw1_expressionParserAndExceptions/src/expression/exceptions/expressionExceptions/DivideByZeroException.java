package expression.exceptions.expressionExceptions;

public class DivideByZeroException extends EvaluatingException {
    public DivideByZeroException (final String string) {
        super(string);
    }
}
