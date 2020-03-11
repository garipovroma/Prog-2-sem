package expression.exceptions;

import expression.parser.BaseParser;

public class ParsingException extends Exception {
    public ParsingException(String string) {
        super(string);
    }
    public static String createErrorMessage(String errorBegin, BaseParser parser) {
        String result = errorBegin + " - near pos = " + parser.getPos() +
                ", it might be the cause of exception : " + parser.getSubstringWithError();
        return result;
    }
}
