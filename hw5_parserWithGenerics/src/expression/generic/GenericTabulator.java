package expression.generic;

import expression.TripleExpression;
import expression.calculationType.*;
import expression.exceptions.EvaluatingException;
import expression.parser.ExpressionParser;

import java.util.Map;

public class GenericTabulator implements Tabulator {
    private final Map<String, CalculationType<? extends Number>> operationByMode = Map.of(
            "i", new IntegerCalculationType(true),
            "d", new DoubleCalculationType(true),
            "bi", new BigIntegerCalculationType(true),
            "u", new IntegerCalculationType(false),
            "l", new LongCalculationType(false),
            "s", new ShortCalculationType(false)
            );

    @Override
    public Object[][][] tabulate(String mode, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        CalculationType<? extends Number> calculationType = operationByMode.get(mode);
        return getTable(calculationType, expression, x1, x2, y1, y2, z1, z2);
    }

    private <T extends Number> Object[][][] getTable(CalculationType<T> calculationType, String expression, int x1, int x2, int y1, int y2, int z1, int z2) throws Exception {
        ExpressionParser <T> parser = new ExpressionParser<>(calculationType);
        TripleExpression <T> result = parser.parse(expression);
        int dx = x2 - x1 + 1;
        int dy = y2 - y1 + 1;
        int dz = z2 - z1 + 1;
        Object[][][] mas = new Object[dx][dy][dz];
        for (int i = 0; i < dx; i++) {
            for (int j = 0; j < dy; j++) {
                for (int k = 0; k < dz; k++) {
                    T x = calculationType.parse(Integer.toString(x1 + i));
                    T y = calculationType.parse(Integer.toString(y1 + j));
                    T z = calculationType.parse(Integer.toString(z1 + k));
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
