package currencies.structures.expressions;

import currencies.lexer.Token;

public class Comparison extends RValue {
    private RValue rightOperand;
    private Token operator;
    private RValue leftOperand;

    public Comparison(RValue rightOperand, Token operator, RValue leftOperand) {
        this.rightOperand = rightOperand;
        this.operator = operator;
        this.leftOperand = leftOperand;
    }

    @Override
    public String toString() {
        return "(" + rightOperand + " " + operator.getValue() + " " + leftOperand + ")";
    }
}
