package expression.exceptions;

public class InvalidArgumentsException extends EvaluatingException {
    public InvalidArgumentsException(String string) {
        super(string);
    }
}
