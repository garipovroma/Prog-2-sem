package expression.parser;

public class BaseParser {
    private ExpressionSource source;
    protected char ch;
    public void createSource(StringSource source) {
        this.source = source;
    }
    protected void nextChar() {
        ch = source.hasNext() ? source.next() : '\0';
    }
    public void skipWhitespaces() {
        while (Character.isWhitespace(ch)) {
            nextChar();
        }
    }
    public boolean check(char x) {
        return (between(x, x));
    }
    public boolean check (String s) {
        int n = s.length();
        for (int i = 0; i < n; i++) {
            if (check(s.charAt(i))) {
                nextChar();
            } else {
                return false;
            }
        }
        return true;
    }
    protected boolean between(char l, char r) {
        return (l <= ch && ch <= r);
    }
    public int getPos() {
        return source.getPos();
    }
    public String getSubstringWithError() {
        return source.getSubstringWithError();
    }
    protected boolean test(char expected) {
        if (ch == expected) {
            nextChar();
            return true;
        }
        return false;
    }
}
