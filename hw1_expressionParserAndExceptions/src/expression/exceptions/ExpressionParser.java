package expression.exceptions;

import expression.*;
import expression.exceptions.expressionExceptions.*;
import expression.exceptions.operators.*;

import java.util.Map;
import java.util.Set;

public class ExpressionParser extends BaseParser implements Parser {
    Token curToken = Token.NUM;
    private int constValue;
    private String curString;
    private boolean isNegative = false;
    private static final Set<String> variablesName = Set.of(
            "x", "y", "z"
    );
    private static final int highestPriority = 2;
    private static final Map<String, Token> operatorsWithStrName = Map.of(
            "log2", Token.LOG2,
            "pow2", Token.POW2
    );
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
    private final int lowestPriority = -1;
    private Token getConst() throws ParsingException {
        StringBuilder value = new StringBuilder();
        if (isNegative) {
            value.append("-");
            isNegative = false;
        }
        while(between('0', '9')) {
            value.append(ch);
            nextChar();
        }
        try {
            constValue = Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            throw new IllegalConstantException(ParsingException.createErrorMessage(
                    "Illegal constant :" + value.toString(), this));
        }
        return curToken = Token.NUM;
    }
    private String getString() {
        StringBuilder value = new StringBuilder();
        while (Character.isAlphabetic(ch) || Character.isDigit(ch)) {
            value.append(ch);
            nextChar();
        }
        return value.toString();
    }
    private Token getToken() throws ParsingException {  // before all of reading from source - do skipWhitespaces();
        skipWhitespaces();
        if (between('0', '9')) {
            return getConst();
        }
        switch (ch) {
            case '\0':
                return curToken = Token.END;
            case '*':
                if (check("**")) {
                    return curToken = Token.POW;
                }
                return curToken = Token.MUL;
            case '/':
                if (check("//")) {
                    return curToken = Token.LOG;
                }
                return curToken = Token.DIV;
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
                    curString = getString();
                    if (variablesName.contains(curString)) {
                        return curToken = Token.NAME;
                    } else if (operatorsWithStrName.containsKey(curString)) {
                        return curToken = operatorsWithStrName.get(curString);
                    } else {
                        throw new InvalidOrMissedExpressionException(ParsingException.createErrorMessage(
                                curString.toString() +
                                        " - undefined variable or operator", this));
                    }
                } else {
                    throw new InvalidOrMissedExpressionException(ParsingException.createErrorMessage(
                            ch + " - invalid or missed variable", this));
                }

        }
    }
    @Override
    public CommonExpression parse(String expression) throws ParsingException {
        createSource(new StringSource(expression));
        nextChar();
        getToken();
        return parseExpression(highestPriority, false, false);
    }
    private CommonExpression parsePrimeExpression(boolean get, boolean needBracket) throws ParsingException {
        if (get) {
            getToken();
        }
        CommonExpression res = null;
        skipWhitespaces();
        switch (curToken) {
            case NAME:
                res = new Variable(curString);
                getToken();
                break;
            case NUM:
                res = new Const(constValue);
                getToken();
                break;
            case SUB:
                if (between('0', '9')) {
                    isNegative = true;
                    getToken();
                    res = new Const(constValue);
                    getToken();
                    return res;
                }
                res = CheckedNegate.getNegative(parsePrimeExpression(true, needBracket));
                break;
            case LOG2:
                if (between('0', '9')) {
                    getToken();
                    res = CheckedLog2.getLog2(new Const(constValue));
                    getToken();
                    return res;
                }
                return CheckedLog2.getLog2(parsePrimeExpression(true, needBracket));
            case POW2:
                if (between('0', '9')) {
                    getToken();
                    res = CheckedPow2.getPow2(new Const(constValue));
                    getToken();
                    return res;
                }
                return CheckedPow2.getPow2(parsePrimeExpression(true, needBracket));
            case LBRACKET:
                res = parseExpression(highestPriority, true, true);
                if (curToken != Token.RBRACKET) {
                    throw new BracketException(ParsingException.createErrorMessage(
                            "Bracket not found after :" + res.toString(), this));
                }
                getToken();
                break;
            case RBRACKET:
                throw new BracketException(ParsingException.createErrorMessage(
                            "invalid or missed expression : ", this));
            default:
                throw new InvalidOrMissedExpressionException(ParsingException.createErrorMessage(
                        ch + " - invalid or missed expression : ", this));
        }
        return res;
    }
    private CommonExpression parseExpression(int priority, boolean get, boolean needBracket) throws ParsingException {
        if (priority == lowestPriority) {
            return parsePrimeExpression(get, needBracket);
        } else {
            CommonExpression res = parseExpression(priority - 1, get, needBracket);
            for ( ; ; ) {
                Token curTok = curToken;
                if (!operators.contains(curToken) && curToken != Token.END && curToken != Token.RBRACKET) {
                    throw new InvalidOrMissedExpressionException(ParsingException.createErrorMessage(
                            ch + " - unexpected sign or missed argument : ", this));
                }
                if (getOperationsByPriority.get(priority).contains(curToken)) {
                    CommonExpression curExpression = parseExpression(priority - 1,
                            true,  needBracket);
                    res = makeExpression(res, curExpression, curTok);
                } else {
                    break;
                }
            }
            if (needBracket && curToken != Token.RBRACKET && priority == highestPriority) {
                throw new BracketException(ParsingException.createErrorMessage(
                        "Expected )", this));
            }
            if (!needBracket && curToken == Token.RBRACKET && priority == highestPriority) {
                throw new BracketException(ParsingException.createErrorMessage(
                        "Unexpected )", this));
            }
            return res;
        }
    }
    private CommonExpression makeExpression(CommonExpression left, CommonExpression right, Token operator) throws ParsingException {
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
        throw new UndefinedOperatorException(ParsingException.createErrorMessage(
                operator + "- undefined operator", this));
    }
}
