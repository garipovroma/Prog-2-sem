package expression.exceptions;

public class IllegalConstantException extends ParsingException {
    public IllegalConstantException(String string) {
        super(string);
    }
}
