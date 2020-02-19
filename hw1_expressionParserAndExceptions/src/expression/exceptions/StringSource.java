package expression.exceptions;

import expression.exceptions.expressionExceptions.ExpressionException;

public class StringSource implements ExpressionSource {
    private final String data;
    private int pos;

    public StringSource(String data) {
        this.data = data;
        this.pos = 0;
    }
    private void skipWhitespaces() {
        while (pos < data.length() && Character.isWhitespace(data.charAt(pos))) {
            pos++;
        }
    }
    @Override
    public int getPos() {
        return pos;
    }
    @Override
    public String getSubstringWithError() {
        int l = pos - 10;
        if (l < 0) {
            l = 0;
        }
        int r = pos + 10;
        if (r > data.length()) {
            r = data.length();
        }
        return data.substring(l, r);
    }
    @Override
    public boolean hasNext() {
        return pos < data.length();
    }

    @Override
    public char next() {
        return data.charAt(pos++);
    }
    @Override
    public ExpressionException error(String message) {
        return new ExpressionException(pos + ": " + message);
    }
}
