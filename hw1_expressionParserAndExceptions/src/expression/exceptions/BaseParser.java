package expression.exceptions;

public class BaseParser {
    private ExpressionSource source;
    protected char ch;
    public void createSource(StringSource source) {
        this.source = source;
    }
    protected void commonNextChar() {
        ch = source.commonHasNext() ? source.commonNext() : '\0';
    }
    protected void nextChar() {
        ch = source.hasNext() ? source.next() : '\0';
    }
    protected void skipWhitespaces() {
        while (ch != '\0' && Character.isWhitespace(ch)) {
            commonNextChar();
        }
    }
    protected int getPos() {
        return source.getPos();
    }
    protected String getSubstringWithError() {
        return source.getSubstringWithError();
    }
    protected boolean test(char expected) {
        if (ch == expected) {
            nextChar();
            return true;
        }
        return false;
    }
    protected boolean check(String s) {
        int n = s.length();
        for (int i = 0; i < n; i++) {
            if (ch == s.charAt(i)) {
                commonNextChar();
            } else {
                skipWhitespaces();
                return false;
            }
        }
        nextChar();
        return true;
    }
    protected boolean testBetween(char l, char r) {
        if (l <= ch && ch <= r) {
            return true;
        }
        return false;
    }
    protected void expect(final char value) {
        if (ch != value) {
            throw error("Expected '" + value + "' , found '" + ch + "'");
        }
        nextChar();
    }
    protected void expect(final String value) {
        for (char c : value.toCharArray()) {
            expect(c);
        }
    }
    protected RuntimeException error(final String message) {
        return source.error(message);
    }
    protected boolean between(char l, char r) {
        return (l <= ch && ch <= r);
    }
}
