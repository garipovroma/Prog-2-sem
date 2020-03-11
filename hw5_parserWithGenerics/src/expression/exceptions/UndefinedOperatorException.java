package expression.exceptions;

public class UndefinedOperatorException extends ParsingException {
    public UndefinedOperatorException(String string) {
        super(string);
    }
}
