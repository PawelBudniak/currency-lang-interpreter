package currencies.structures.expressions;

import currencies.lexer.Token;

import java.util.List;

public class ArithmeticExpression extends RValue {
    private List<ArithmeticTerm> operands;
    private List<Token> operators;
    private Object value;

    public ArithmeticExpression(List<ArithmeticTerm> operands, List<Token> operators) {
        this.operands = operands;
        this.operators = operators;
    }


    @Override
    public String toString () {
        return exprToStr(operands, operators);
    }

}
