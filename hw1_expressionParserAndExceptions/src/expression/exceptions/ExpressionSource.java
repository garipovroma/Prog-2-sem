package expression.exceptions;

public interface ExpressionSource {
    boolean hasNext();
    char next();
    char commonNext();
    boolean commonHasNext();
    int getPos();
    String getSubstringWithError();
    ExpressionException error(final String message);
}
