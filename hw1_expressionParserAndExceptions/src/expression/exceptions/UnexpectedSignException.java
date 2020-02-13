package expression.exceptions;

public class UnexpectedSignException extends ParsingException {
    public UnexpectedSignException(String string) {
        super(string);
    }
}
