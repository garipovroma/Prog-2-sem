package expression.exceptions;

public class ExpressionException extends RuntimeException {
    public ExpressionException(String string) {
        super(string);
    }
    public static String createErrorMessage(String errorBegin, BaseParser parser) {
        String result = errorBegin + " - near pos = " + parser.getPos() +
                ", it might be the cause of exception : " + parser.getSubstringWithError();
        return result;
    }
}
