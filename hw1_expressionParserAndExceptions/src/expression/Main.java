package expression;

import expression.exceptions.*;

public class Main {
    public static void main(String args[]) throws Exception {
  /*      CommonExpression e = new CheckedDivide(new CheckedMultiply(new Const(1000000), new CheckedMultiply(
                new Variable("x"), new CheckedMultiply(new Variable("x"),
                new CheckedMultiply(new Variable("x"), new Variable("x"))))),
                new CheckedSubtract(new Variable("x"), new Const(1)));
*/


       /* Parser myParser = new ExpressionParser();
        CommonExpression e = (CommonExpression) myParser.parse("1000000*x*x*x*x*x/(x-1)");
        System.out.println(e.toMiniString());
        for (int i = 0; i <= 10; i++) {
            System.out.print(i + " : ");
            try {
                System.out.println(e.evaluate(i));
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }*/
       Parser myParser = new ExpressionParser();
       //CommonExpression e = (CommonExpression) myParser.parse("((2)+2))");
         //CommonExpression e = (CommonExpression) myParser.parse("10 20");
        //CommonExpression e = (CommonExpression) myParser.parse("10 20");
        CommonExpression e = (CommonExpression) myParser.parse("-(-(-\t\t-5 + 16   *x*y) + 1 * z) -(((-11)))");
       System.out.println(e.evaluate(0));
    }
}
