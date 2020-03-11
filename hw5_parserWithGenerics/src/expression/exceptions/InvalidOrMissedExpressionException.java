package expression.exceptions;

public class InvalidOrMissedExpressionException extends ParsingException {
    public InvalidOrMissedExpressionException(String string) {
        super(string);
    }
}
