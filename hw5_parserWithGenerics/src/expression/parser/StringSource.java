package expression.parser;

public class StringSource implements ExpressionSource {
    private final String data;
    private int pos;

    public StringSource(String data) {
        this.data = data;
        this.pos = 0;
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
}
