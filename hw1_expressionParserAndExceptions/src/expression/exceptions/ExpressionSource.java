package expression.exceptions;

public interface ExpressionSource {
    boolean hasNext();
    char next();
    int getPos();
    String getSubstringWithError();
}
