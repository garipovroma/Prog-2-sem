package jstest.object;

import jstest.ArithmeticTests;
import jstest.Language;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public class ObjectMinMaxTest extends ObjectExpressionTest {
    public static final Dialect MIN_MAX_DIALECT = ObjectExpressionTest.ARITHMETIC_DIALECT.copy()
            .rename("min3", "Min3")
            .rename("max5", "Max5");

    public static class MinMaxTests extends ArithmeticTests {{
        any("min3", 3, lift(args -> args.min(Double::compare)));
        any("max5", 5, lift(args -> args.max(Double::compare)));
        tests(
                f("min3", vx, vy, vz),
                f("max5", vx, vy, vz, c(7), f("*", vy, vz)),
                f("min3", vx, f("-", vy, vz), c(7))/*,
                f("/",
                        f("/",
                                f("-", vz, vz),
                                f("+",
                                        vx,
                                        f("-", vz, c(1777340624))
                                )
                        ),
                        f("-", vy, vy))
                        */
        );
    }}

    protected ObjectMinMaxTest(final int mode, final Language language) {
        super(mode, language);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private static Function<List<Double>, Double> lift(final Function<Stream<Double>, Optional<Double>> f) {
        return args -> args.stream().anyMatch(v -> Double.isNaN(v))
                ? Double.NaN
                : f.apply(args.stream()).get();
    }

    public static void main(final String... args) {
        test(ObjectMinMaxTest.class, ObjectMinMaxTest::new, new MinMaxTests(), args, MIN_MAX_DIALECT);
    }
}
