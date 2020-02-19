package expression.exceptions;

import expression.exceptions.expressionExceptions.ExpressionException;

public interface ExpressionSource {
    boolean hasNext();
    char next();
    int getPos();
    String getSubstringWithError();
    ExpressionException error(final String message);
}
