package expression.exceptions.expressionExceptions;

public class OverflowException extends EvaluatingException {
    public OverflowException (final String string) {
        super(string);
    }
    public static boolean checkOverflow(int left, int right, String operator) {
        switch (operator) {
            case "+":
                int val = left + right;
                if ((left > 0 && right > 0 && val <= 0) || (left < 0 && right < 0 && val >= 0)) {
                    return true;
                }
                break;
            case "-":
                val = left - right;
                if ((left > 0 && right < 0 && val <= 0) || (left < 0 && right > 0 && val >= 0) ||
                        (left == 0 && right == Integer.MIN_VALUE)) {
                    return true;
                }
                break;
            case "*":
            case "**":
                if (left != 0 && right != 0 && ((left * right) / right != left || (left * right) / left != right)) {
                    return true;
                }
                break;
            case "/":
            case "//":
                if (left == Integer.MIN_VALUE && right == -1) {
                    return true;
                }
        }
        return false;
    }
}
