package expression.exceptions;

public class PowOverflowException extends OverflowException {
    public PowOverflowException(String string) {
        super(string);
    }
}
