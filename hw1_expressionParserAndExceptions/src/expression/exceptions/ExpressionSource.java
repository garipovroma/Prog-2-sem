package expression.exceptions;

public interface ExpressionSource {
    boolean hasNext();
    char next();
    char commonNext();
    boolean commonHasNext();
    ExpressionException error(final String message);
}
