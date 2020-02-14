package expression.exceptions.expressionExceptions;

public class UndefinedVariableOrOperatorException extends ParsingException {
    public UndefinedVariableOrOperatorException(String string) {
        super(string);
    }
}
