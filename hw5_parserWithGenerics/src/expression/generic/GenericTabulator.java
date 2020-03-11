package expression.generic;

import expression.TripleExpression;
import expression.exceptions.EvaluatingException;
import expression.exceptions.ParsingException;
import expression.operations.BigIntegerOperation;
import expression.operations.DoubleOperation;
import expression.operations.IntegerOperation;
import expression.operations.Operation;
import expression.parser.ExpressionParser;

import java.math.BigInteger;
import java.util.Map;

public class GenericTabulator implements Tabulator {
    private final Map<String, Operation<? extends Number>> operationByMode = Map.of(
            "i", new IntegerOperation(),
            "d", new DoubleOperation(),
            "bi", new BigIntegerOperation()
            );

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        Operation <? extends Number> operation = operationByMode.get(mode);
        return getTable(operation, expression, x1, x2, y1, y2, z1, z2);
    }

    private <T extends Number> Object[][][] getTable(Operation<T> operation, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        ExpressionParser <T> parser = new ExpressionParser<>(operation);
        TripleExpression <T> result = parser.parse(expression);
        int dx = x2 - x1 + 1;
        int dy = y2 - y1 + 1;
        int dz = z2 - z1 + 1;
        Object[][][] mas = new Object[dx][dy][dz];
        for (int i = 0; i < dx; i++) {
            for (int j = 0; j < dy; j++) {
                for (int k = 0; k < dz; k++) {
                    T x = operation.parse(Integer.toString(x1 + i));
                    T y = operation.parse(Integer.toString(y1 + j));
                    T z = operation.parse(Integer.toString(z1 + k));
                    try {
                        mas[i][j][k] = result.evaluate(x, y, z);
                    } catch (EvaluatingException e) {
                        mas[i][j][k] = null;
                    }

                }
            }
        }
        return mas;
    }
}
