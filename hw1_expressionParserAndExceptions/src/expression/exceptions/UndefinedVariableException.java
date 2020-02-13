package expression.exceptions;

public class UndefinedVariableException extends ParsingException {
    public UndefinedVariableException(String string) {
        super(string);
    }
}
