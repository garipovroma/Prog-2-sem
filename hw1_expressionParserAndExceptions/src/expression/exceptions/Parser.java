package expression.exceptions;

import expression.TripleExpression;
import expression.exceptions.expressionExceptions.ParsingException;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public interface Parser {
    TripleExpression parse(String expression) throws ParsingException;
}
