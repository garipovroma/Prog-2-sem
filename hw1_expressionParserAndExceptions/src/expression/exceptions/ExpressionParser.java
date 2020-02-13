package expression.exceptions;

import expression.*;

import java.util.Map;
import java.util.Set;

public class ExpressionParser extends BaseParser implements Parser {
    Token curToken = Token.NUM;
    Token prevToken = Token.NUM;
    private int curConst;
    private String variableName;
    private boolean getNegativeConst = false;
    private static final Set<String> variablesName = Set.of(
            "x", "y", "z"
    );

    private static final int highestPriority = 2;
    private static final Map<Token, Integer> priority = Map.of(
            Token.MUL, 1,
            Token.DIV, 1,
            Token.ADD, 2,
            Token.SUB, 2,
            Token.POW, 0,
            Token.LOG, 0
    );
    private static final Map<Integer, Set<Token>> getOperationsByPriority = Map.of(
            1, Set.of(Token.MUL, Token.DIV),
            2, Set.of(Token.ADD, Token.SUB),
            0, Set.of(Token.POW, Token.LOG)
    );
    private static final Set<Token> operators = Set.of(
            Token.ADD, Token.SUB, Token.DIV, Token.MUL, Token.POW, Token.LOG
    );
    private static final Map<Token, String> getOperator = Map.of(
            Token.ADD, "+",
            Token.SUB, "-",
            Token.MUL, "*",
            Token.DIV, "/",
            Token.POW, "**",
            Token.LOG, "//"
    );
    private static final int lowestPriority = -1;

    @Override
    public CommonExpression parse(String expression) throws ParsingException {
        createSource(new StringSource(expression));
        nextChar();
        getToken();
        CommonExpression commonExpression = parseExpression(highestPriority, false, false);
        return commonExpression;
    }
    private Token getConst() {
        StringBuilder value = new StringBuilder();
        if (getNegativeConst) {
            value.append("-");
            getNegativeConst = false;
        }
        skipWhitespaces();
        while (between('0', '9')) {
            value.append(ch);
            commonNextChar();
        }
        skipWhitespaces();
        try {
            curConst = Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            throw new IllegalConstantException(ExpressionException.createErrorMessage(
                    "Illegal constant :" + value.toString(), this));
        }
        return curToken = Token.NUM;
    }
    private Token getToken() {
        prevToken = curToken;
        if (between('0', '9')) {
            skipWhitespaces();
            return getConst();
        } else {
            switch  (ch) {
                case '\0':
                    return curToken = Token.END;
                case '*':
                    commonNextChar();
                    if (ch == '*') {
                        nextChar();
                        return curToken = Token.POW;
                    } else {
                        skipWhitespaces();
                        return curToken = Token.MUL;
                    }
                case '/':
                    commonNextChar();
                    if (ch == '/') {
                        nextChar();
                        return curToken = Token.LOG;
                    } else {
                        skipWhitespaces();
                        return curToken = Token.DIV;
                    }
                case '+':
                    nextChar();
                    return curToken = Token.ADD;
                case '-':
                    nextChar();
                    return curToken = Token.SUB;
                case '(':
                    nextChar();
                    return curToken = Token.LBRACKET;
                case ')':
                    nextChar();
                    return curToken = Token.RBRACKET;
                default:
                    if (Character.isAlphabetic(ch)) {
                        StringBuilder name = new StringBuilder();
                        skipWhitespaces();
                        while (Character.isAlphabetic(ch)) {
                            name.append(ch);
                            commonNextChar();
                        }
                        skipWhitespaces();
                        variableName = name.toString();
                        if (variablesName.contains(variableName)) {
                            return curToken = Token.NAME;
                        } else {
                            throw new UndefinedVariableException(ExpressionException.createErrorMessage(
                                    variableName.toString() +
                                    " - undefined variable", this));
                        }
                    } else {
                        throw new UnexpectedSignException(ExpressionException.createErrorMessage(
                                ch + " - undefined sign", this));
                    }
            }
        }
    }

    private CommonExpression parsePrimeExpression(boolean get) {
        if (get) {
            getToken();
        }
        switch (curToken) {
            case NUM:
                CommonExpression cur = new Const(curConst);
                skipWhitespaces();
                getToken();
                return cur;
            case NAME:
                CommonExpression variable = new Variable(variableName);
                getToken();
                return variable;
            case SUB:
                if (testBetween('0', '9')) {
                    getNegativeConst = true;
                    getToken();
                    cur = new Const(curConst);
                    getToken();
                    return cur;
                }
                return CheckedNegate.getNegative(parsePrimeExpression(true));
            case LBRACKET:
                cur = parseExpression(highestPriority, true, true);
                if (curToken != Token.RBRACKET) {
                    throw new BracketException(ExpressionException.createErrorMessage(
                            "Bracket not found after :" + cur.toString(), this));
                }
                getToken();
                return cur;
            default:
                throw new UnexpectedSignException(ExpressionException.createErrorMessage(
                        ch + " - unexpected sign", this));
        }
    }

    private CommonExpression parseExpression(int priority, boolean get, boolean expectedRightBracket) {
        if (priority == lowestPriority) {
            return parsePrimeExpression(get);
        } else {
            CommonExpression res = parseExpression(priority - 1, get, expectedRightBracket);
            for ( ; ; ) {
                Token curTok = curToken;
                if (!operators.contains(curToken) && curToken != Token.END && curToken != Token.RBRACKET) {
                    throw new UnexpectedSignException(ExpressionException.createErrorMessage(
                            getOperator.get(curToken) + " - unexpected sign", this));
                }
                if (getOperationsByPriority.get(priority).contains(curToken)) {
                    CommonExpression curExpression = parseExpression(priority - 1,
                            true, expectedRightBracket);
                    res = makeExpression(res, curExpression, curTok);
                } else {
                    break;
                }
            }
            if (expectedRightBracket && curToken != Token.RBRACKET && priority == highestPriority) {
                throw new BracketException(ExpressionException.createErrorMessage(
                        "Expected )", this));
            }
            if (!expectedRightBracket && curToken == Token.RBRACKET) {
                throw new BracketException(ExpressionException.createErrorMessage(
                        "Unexpected )", this));
            }
            return res;
        }
    }
    private CommonExpression makeExpression(CommonExpression left, CommonExpression right, Token operator) {
        switch (operator) {
            case ADD:
                return new CheckedAdd(left, right);
            case SUB:
                return new CheckedSubtract(left, right);
            case MUL:
                return new CheckedMultiply(left, right);
            case DIV:
                return new CheckedDivide(left, right);
            case POW:
                return new CheckedPow(left, right);
            case LOG:
                return new CheckedLog(left, right);
        }
        throw new UndefinedOperatorException(ExpressionException.createErrorMessage(
                operator + "- undefined operator", this));
    }
}
